package com.nxist.gaokao.view.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.Major;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.AndroidShare;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.UserService;
import com.nxist.gaokao.view.LoginActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MajorDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webView;
    private ImageView collectImageView;//关注
    private ImageView shareImageView;//分享
    private Boolean isCollect=false;//设置关注标记
    private String majorId;//当前专业Id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.major_detail_activity);
        initUI();
    }

    private void initUI(){
        Intent intent = getIntent();
        String majorCode=intent.getStringExtra("majorCode");
        String majorName=intent.getStringExtra("majorName");
        webView = (WebView) findViewById(R.id.major_detail_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        if(majorCode!=null&&!majorCode.equals(""))
            webView.loadUrl(BasicData.SERVER_ADDRESS+"home/majorDetailtoAndroid?majorCode="+majorCode);
        if(majorName!=null&&!majorName.equals(""))
            webView.loadUrl(BasicData.SERVER_ADDRESS+"home/majorDetailtoAndroidByMajorName?majorName="+majorName);
        collectImageView=(ImageView)findViewById(R.id.collect_major_imageView);
        collectImageView.setOnClickListener(this);
        shareImageView=(ImageView)findViewById(R.id.share_major_imageView);
        shareImageView.setOnClickListener(this);
        Major majorPost=new Major();
        majorPost.setMajorCode(majorCode);
        majorPost.setMajorName(majorName);
        Gson gson=new Gson();
        String postJSONString=gson.toJson(majorPost);
        NetworkConnect.postData("home/getMajorId", postJSONString,
                new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        majorId=response.body().toString();//保存上传非图片内容后的话题ID
                        majorId=majorId.substring(0,majorId.lastIndexOf("."));//去除末尾的小数
                        updateCollect();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Toast.makeText(MajorDetailActivity.this,"上传失败！请重试",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.collect_major_imageView://点击关注
                if(!UserService.IsLogin(getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
                    Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(loginIntent);
                    break;
                }
                Log.e("getContext()","是否点击：");
                SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
                int userId=sp.getInt("userId",-1);
                if(isCollect==false){//进行关注操作
                    NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/majorCollect?userId="+userId+"&majorId="+majorId, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("getContext()","成功关注："+response.body());
                            updateCollect();
                            Toast.makeText(MajorDetailActivity.this,"关注成功！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                else{//取消关注
                    NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/majorCancelCollect?userId="+userId+"&majorId="+majorId, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("getContext()","取消关注："+response.body());
                            updateCollect();
                            Toast.makeText(MajorDetailActivity.this,"已取消关注！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                break;
            case R.id.share_major_imageView://点击分享
                //获取当前Activity截图
                final Bitmap bitmap=captureScreen(MajorDetailActivity.this);
                //定义弹出适配器
                ArrayAdapter<String> shareAdapter=new ArrayAdapter<String>(MajorDetailActivity.this,android.R.layout.simple_list_item_1,BasicData.SHARE_ITEM);
                //利用DialogPlus弹出对话框
                DialogPlus dialog = DialogPlus.newDialog(MajorDetailActivity.this)
                        .setAdapter(shareAdapter)//设置适配器
                        .setGravity(Gravity.BOTTOM)//设置位置
                        .setCancelable(true)//设为可取消
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                //得到用户选择的分享项
                                String result=BasicData.SHARE_ITEM[position];
                                AndroidShare androidShare=new AndroidShare(MajorDetailActivity.this);
                                if(result.equals("QQ")){
                                    androidShare.sharedQQ(MajorDetailActivity.this,bitmap);
                                }
                                if(result.equals("微信")){
                                    androidShare.shareWeChatFriend("","",1,bitmap);
                                }
                                if(result.equals("微信朋友圈")){
                                    androidShare.shareWeChatFriendCircle("","",bitmap);
                                }
                                //取消对话框
                                dialog.dismiss();
                            }
                        })
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();
                break;
            default:break;
        }
    }

    //更新关注
    private void updateCollect(){
        //查看该用户否关注该院校
        SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
        int userId=sp.getInt("userId",-1);
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/majorIsCollect?userId="+userId+"&majorId="+majorId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("getContext()","是否关注："+response.body());
                if(response.body().equals("0"))
                    isCollect=false;
                else
                    isCollect=true;
                Log.e("getContext()","更新关注："+isCollect);
                if(isCollect)
                    collectImageView.setImageResource(R.drawable.ic_favorites_filling);
                else
                    collectImageView.setImageResource(R.drawable.ic_favorite);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 获取Activity截图
     * @param activity
     * @return
     */
    public static Bitmap captureScreen(Activity activity) {

        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);

        Bitmap bmp=activity.getWindow().getDecorView().getDrawingCache();

        return bmp;

    }

}
