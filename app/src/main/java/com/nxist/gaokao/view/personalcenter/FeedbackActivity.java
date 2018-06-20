package com.nxist.gaokao.view.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.nxist.gaokao.Module.Feedback;
import com.nxist.gaokao.Module.TopicData;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.CircleTransform;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.PicassoImageLoader;
import com.nxist.gaokao.services.UploadMethod;
import com.nxist.gaokao.view.PublishActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText content;
    private EditText contact;
    private Button confirm;
    private Button cancel;
    private GridView pictureGridView;//宫格视图，存放用户选择的照片
    private GridAdapter gridAdapter;//定义宫格适配器
    private ArrayList<ImageItem> imageItems;//定义图片List
    private int size = 0;//用户要上传的图片张数
    private LinearLayout unloadLayout;//加载动画所在布局
    private final int UNLOAD_OK=0x110;
    String contentString;
    String contactString;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pc_feedback_activity);
        initUI();
    }
    private void initUI(){
        content=(EditText)findViewById(R.id.feedbackContent);
        contact=(EditText)findViewById(R.id.feedbackContact);
        confirm=(Button)findViewById(R.id.feedbackConfirm);
        confirm.setOnClickListener(this);
        cancel=(Button)findViewById(R.id.feedbackCancel);
        cancel.setOnClickListener(this);
        pictureGridView= (GridView) findViewById(R.id.feedbackPicture);
        gridAdapter = new GridAdapter();
        pictureGridView.setAdapter(gridAdapter);

        //设置加载动画
        unloadLayout= (LinearLayout) findViewById(R.id.unloadLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.feedback_spin_kit);//进度条
        FadingCircle doubleBounce = new FadingCircle();//定义样式doubleBounce
        progressBar.setIndeterminateDrawable(doubleBounce);// 设置用于模糊模式的Drawable资源。
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.feedbackConfirm://点击提交
                contentString=content.getText().toString();
                contactString=contact.getText().toString();
                SharedPreferences sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
                userId=sharedPreferences.getInt("userId",-1);
                if (contentString.length() < 1 && size == 0) {
                    showData("反馈不能为空");
                } else {
                    if(userId==-1){
                        showData("请登录！");
                    }else {
                        confirm.setEnabled(false);//发表控件禁用
                        unloadLayout.setVisibility(View.VISIBLE);//显示加载动画
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                upload_database();
                            }
                        }.start();
                    }
                }
                break;
            case R.id.feedbackCancel:
                finish();
                break;
                default:break;
        }
    }

    /**
     * 上传函数
     */
    private void upload_database() {
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        final Message m=new Message();
        m.what=UNLOAD_OK;
        Feedback feedback=new Feedback();
        feedback.setContent(contentString);
        feedback.setContact(contactString);
        feedback.setUserId(userId);
        Gson gson=new Gson();
        if (size == 0) {//用户未发表图片
            String postJSONString=gson.toJson(feedback);
            NetworkConnect.postData("personalcenter/insertFeedbackWithoutPictue", postJSONString,
                    new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            handler.sendMessage(m);//取消上传动画
                            finish();
                            Toast.makeText(FeedbackActivity.this,"你成功上传了数据！"+response.body(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            handler.sendMessage(m);//取消上传动画
                            Toast.makeText(FeedbackActivity.this,"上传失败！请重试",Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);//发表控件启用
                        }
                    });
        }
        else{//如果有发表内容有图片
            size = 0;
            final List<String> fileNames = new ArrayList<>();//定义保存用户要上传图片路径的列表filePaths
            for (int i = 0; i < imageItems.size(); i++) {
                fileNames.add(imageItems.get(i).name);
            }
            feedback.setPicture(fileNames.get(0));
            String postJSONString=gson.toJson(feedback);
            //先发送非图片内容，成功后再上传图片
            NetworkConnect.postData("personalcenter/insertFeedbackWithoutPictue", postJSONString,
                    new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            String feedbackId=response.body().toString();//保存上传非图片内容后的话题ID
                            feedbackId=feedbackId.substring(0,feedbackId.lastIndexOf("."));//去除末尾的小数
                            //上传图片，"_"为文件名分隔符
                            final List<String> filePaths = new ArrayList<>();//定义保存用户要上传图片路径的列表filePaths
                            for (int i = 0; i < imageItems.size(); i++) {
                                filePaths.add(imageItems.get(i).path);
                            }
                            UploadMethod.upLoadPicture("personalcenter/insertFeedbackPicture",feedbackId+"_", filePaths, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    handler.sendMessage(m);//取消上传动画
                                    finish();
                                    Toast.makeText(FeedbackActivity.this,"你成功上传了数据！",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    handler.sendMessage(m);//取消上传动画
                                    Toast.makeText(FeedbackActivity.this,"上传图片等失败！请重试"+t.getMessage(),Toast.LENGTH_SHORT).show();
                                    confirm.setEnabled(true);//发表控件启用
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            handler.sendMessage(m);//取消上传动画
                            Toast.makeText(FeedbackActivity.this,"上传失败！请重试",Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);//发表控件启用
                        }
                    });
        }
    }

    //上传完成取消加载动画
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UNLOAD_OK:
                    unloadLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private class GridAdapter extends BaseAdapter {
        public GridAdapter() {
        }

        @Override
        public int getCount() {
            if (imageItems == null)
                return 1;
            else
                return imageItems.size()+1;
        }

        @Override
        public Object getItem(int i) {
            return imageItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            FeedbackActivity.GridAdapter.ViewHolder holder = null;
            if (view == null) {
                holder = new FeedbackActivity.GridAdapter.ViewHolder();
                view = LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.grid_layout, null);
                holder.image_voice = (ImageView) view.findViewById(R.id.gird_img);
                view.setTag(holder);
            } else {
                holder = (FeedbackActivity.GridAdapter.ViewHolder) view.getTag();
            }
            if (imageItems == null) {//如果没图，添加添加图标
                holder.image_voice.setImageResource(R.drawable.ic_add);
            } else {
                if (i == imageItems.size()) {//如果是末尾添加添加图标
                    holder.image_voice.setImageResource(R.drawable.ic_add);
                } else {//将选择图片缩放放入宫格视图中
                    File file = new File(imageItems.get(i).path);
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(imageItems.get(i).path);
                        holder.image_voice.setImageBitmap(CircleTransform.centerSquareScaleBitmap(bm,100));
                    }
                }
            }
            holder.image_voice.setOnClickListener(new View.OnClickListener() {//给缩略图添加点击事件
                @Override
                public void onClick(View v) {
                    if ((imageItems != null && i == imageItems.size()) || imageItems == null) {//如果点击加号
                        addImage();
                    }
                }
            });
            return view;
        }

        class ViewHolder {
            private ImageView image_voice;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//得到选择图片窗口返回的数据
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                gridAdapter.notifyDataSetChanged();
                size=imageItems.size();//设置选中的图片数量
            } else {
                showData("没有选择图片");
            }
        }
    }

    /**
     * 添加图片
     */
    private void addImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());
        imagePicker.setMultiMode(true);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSelectLimit(1);    //最多选择1张
        imagePicker.setCrop(false);       //不进行裁剪
        Intent intent = new Intent(FeedbackActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    private void showData(String data){
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }
}
