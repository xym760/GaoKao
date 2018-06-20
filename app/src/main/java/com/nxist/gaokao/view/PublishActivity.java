package com.nxist.gaokao.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.nxist.gaokao.Module.TopicData;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.CircleTransform;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.PicassoImageLoader;
import com.nxist.gaokao.services.UploadMethod;
import com.nxist.gaokao.services.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发表话题
 */
public class PublishActivity extends AppCompatActivity {

    private EditText publishSay;//用户输入的文字内容
    private GridView publishGridView;//宫格视图，存放用户选择的照片
    private GridAdapter gridAdapter;//定义宫格适配器
    private TextView upload;//右上角发表文字
    private int size = 0;//用户要上传的图片张数
    private String mySay;//用户输入的文字内容字符串
    private LinearLayout unloadLayout;//加载动画所在布局
    private ArrayList<ImageItem> imageItems;//定义图片List
    private final int UNLOAD_OK=0x110;

    private LocationManager locationManager;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.publishedToolbar);//顶部标题栏
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_small);//增加返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//点击返回退出发表话题
            }
        });
        intiView();
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
    private void intiView() {
        publishSay= (EditText) findViewById(R.id.publishSay);
        upload= (TextView) findViewById(R.id.upload);//发表

        //设置加载动画
        unloadLayout= (LinearLayout) findViewById(R.id.unloadLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);//进度条
        FadingCircle doubleBounce = new FadingCircle();//定义样式doubleBounce
        progressBar.setIndeterminateDrawable(doubleBounce);// 设置用于模糊模式的Drawable资源。

        publishGridView= (GridView) findViewById(R.id.publishGridView);
        gridAdapter = new GridAdapter();
        publishGridView.setAdapter(gridAdapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(PublishActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 100);
        }else{
            getLocation();
        }

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {//给发表设置点击事件
            @Override
            public void onClick(View view) {
                mySay = publishSay.getText().toString();
                if (mySay.length() < 1 && size == 0) {
                    showData("发表不能为空");
                } else {
                    upload.setEnabled(false);//发表控件禁用
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
        });
    }

    //Android6.0及以上申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    getLocation();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void getLocation(){
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return ;
        }
        Log.d("PublishActivity.this", "位置:"+locationProvider);
        try{
            //获取Location
            Location location = locationManager.getLastKnownLocation(locationProvider);
            Log.d("PublishActivity.this", "位置location:"+location);
            if(location!=null){
                //不为空,显示地理位置经纬度
                locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);
                showLocation(location);
            }
            else{
                locationManager.requestLocationUpdates("gps", 60000, 1, locationListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
    }

    /**
     * 显示地理位置经度和纬度信息
     * @param location
     */
    private void showLocation(Location location){
        String locationStr = "维度：" + location.getLatitude() +"\n"
                + "经度：" + location.getLongitude();
        Log.d("PublishActivity.this", "位置:"+locationStr);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            showLocation(location);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //移除监听器
            locationManager.removeUpdates(locationListener);
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
        final TopicData say = new TopicData();
        SharedPreferences sp=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        int userId=sp.getInt("userId",-1);
        Log.d("PublishActivity.this", "发表用户名:"+userId);
        say.setUserId(userId);
        say.setContent(mySay);//上传用户输入内容
        Gson gson=new Gson();
        if (size == 0) {//用户未发表图片
            say.setIsHavePicture(false);
            String postJSONString=gson.toJson(say);
            NetworkConnect.postData("exchange/insertTopicWithoutPictue", postJSONString,
                    new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            handler.sendMessage(m);//取消上传动画
                            finish();
                            Toast.makeText(PublishActivity.this,"你成功上传了数据！"+response.body(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            handler.sendMessage(m);//取消上传动画
                            Toast.makeText(PublishActivity.this,"上传失败！请重试",Toast.LENGTH_SHORT).show();
                            upload.setEnabled(true);//发表控件启用
                        }
                    });
        }
        else{//如果有发表内容有图片
            size = 0;
            say.setIsHavePicture(true);
            final List<String> fileNames = new ArrayList<>();//定义保存用户要上传图片路径的列表filePaths
            for (int i = 0; i < imageItems.size(); i++) {
                fileNames.add(imageItems.get(i).name);
            }
            say.setPictureList(fileNames);
            String postJSONString=gson.toJson(say);
            //先发送非图片内容，成功后再上传图片
            NetworkConnect.postData("exchange/insertTopicWithoutPictue", postJSONString,
                    new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            String topicId=response.body().toString();//保存上传非图片内容后的话题ID
                            topicId=topicId.substring(0,topicId.lastIndexOf("."));//去除末尾的小数
                            //上传图片，"_"为文件名分隔符
                            final List<String> filePaths = new ArrayList<>();//定义保存用户要上传图片路径的列表filePaths
                            for (int i = 0; i < imageItems.size(); i++) {
                                filePaths.add(imageItems.get(i).path);
                            }
                            UploadMethod.upLoadPicture("exchange/insertTopicPicture",topicId+"_", filePaths, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    handler.sendMessage(m);//取消上传动画
                                    finish();
                                    Toast.makeText(PublishActivity.this,"你成功上传了数据！",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    handler.sendMessage(m);//取消上传动画
                                    Toast.makeText(PublishActivity.this,"上传图片等失败！请重试"+t.getMessage(),Toast.LENGTH_SHORT).show();
                                    upload.setEnabled(true);//发表控件启用
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            handler.sendMessage(m);//取消上传动画
                            Toast.makeText(PublishActivity.this,"上传失败！请重试",Toast.LENGTH_SHORT).show();
                            upload.setEnabled(true);//发表控件启用
                        }
                    });
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
            GridAdapter.ViewHolder holder = null;
            if (view == null) {
                holder = new GridAdapter.ViewHolder();
                view = LayoutInflater.from(PublishActivity.this).inflate(R.layout.grid_layout, null);
                holder.image_voice = (ImageView) view.findViewById(R.id.gird_img);
                view.setTag(holder);
            } else {
                holder = (GridAdapter.ViewHolder) view.getTag();
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
    /**
     * 添加图片
     */
    private void addImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());
        imagePicker.setMultiMode(true);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSelectLimit(9);    //最多选择9张
        imagePicker.setCrop(false);       //不进行裁剪
        Intent intent = new Intent(PublishActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }
    private void showData(String data){
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }
}
