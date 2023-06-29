package com.nxist.gaokao.view.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.User;
import com.nxist.gaokao.Module.UserItem;
import com.nxist.gaokao.Module.UserItemAdapter;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.PicassoImageLoader;
import com.nxist.gaokao.services.UpdateUserInfo;
import com.nxist.gaokao.services.UploadMethod;
import com.nxist.gaokao.services.UserService;
import com.nxist.gaokao.view.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView profileImage;
    private RecyclerView recyclerView;
    private List<UserItem> userItemList=new ArrayList<>();
    private User user;
    SharedPreferences sp;
    private UserItemAdapter userItemAdapter;
    private Button logout;
    private ArrayList<ImageItem> imageItems;//定义图片List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!UserService.IsLogin(getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
            Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        setContentView(R.layout.pc_user_info_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.userinfoToolbar);//顶部标题栏
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_small);//增加返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//点击返回退出发表话题
            }
        });
        initUI();
    }

    private void initUI(){
        profileImage=(CircleImageView)findViewById(R.id.profile_image_update);
        recyclerView=(RecyclerView)findViewById(R.id.personalCenterListDetail);
        profileImage.setOnClickListener(this);
        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(this);
        initList();
        LinearLayoutManager layoutManager=new LinearLayoutManager(UserInfoActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(UserInfoActivity.this, DividerItemDecoration.VERTICAL));
        //设置头像
        setAvatar();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.logout:
                Log.d("getActivity()", "app重启前: ");
                UserService.deleteSharedPreferences(getSharedPreferences("userinfo", Context.MODE_PRIVATE));
                //重启APP
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.profile_image_update:
                addImage();
                break;
            default:break;
        }
    }

    private void setAvatar(){
        sp=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if(sp.getString("avatar","").equals("")){//用户服务器端未设置头像

        }else{
            String urlImg= BasicData.USER_AVATAR_URL+sp.getInt("userId",0)+"_"+sp.getString("avatar","");
            Picasso.with(UserInfoActivity.this).load(urlImg).placeholder(R.drawable.ic_help).into(profileImage);
        }
    }

    private void initList(){
        //从SharedPreferences中读取用户数据
        sp=getApplicationContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        int userId=sp.getInt("userId",-1);
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "user/getUserInfo?userId="+userId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("getActivity()", "userInfo是？: "+response.body().toString());
                //使用Gson解析后台JSON字符串
                Gson gson=new Gson();
                try{
                    //构建topicDataList
                    user=gson.fromJson(response.body().toString(),User.class);
                    userItemList.removeAll(userItemList);
                    userItemList.add(new UserItem("账号",user.getUserAccount()));
                    userItemList.add(new UserItem("姓名",user.getUserName()));
                    userItemList.add(new UserItem("省份",user.getProvince()));
                    userItemList.add(new UserItem("科目",user.getSubject()));
                    userItemList.add(new UserItem("成绩",String.valueOf(user.getScore())));
                    userItemList.add(new UserItem("修改密码"));
                    userItemAdapter=new UserItemAdapter(userItemList);
                    recyclerView.setAdapter(userItemAdapter);
                }
                catch (Exception e){//发生了JSON解析错误等其它异常
                    e.printStackTrace();Log.d("getActivity()", "解析异常:");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//得到选择图片窗口返回的数据
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                //上传图片，"_"为文件名分隔符
                final List<String> filePaths = new ArrayList<>();//定义保存用户要上传图片路径的列表filePaths
                for (int i = 0; i < imageItems.size(); i++) {
                    filePaths.add(imageItems.get(i).path);
                }
                SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
                int userId=sp.getInt("userId",-1);
                UploadMethod.upLoadPicture("user/insertUserAvatarkPicture",userId+"_", filePaths, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //将用户更改的用户名进行存储
                        SharedPreferences.Editor editor=getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                        editor.putString("avatar",filePaths.get(0).substring(filePaths.get(0).lastIndexOf("/")+1,filePaths.get(0).length()));
                        editor.apply();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(UserInfoActivity.this,"上传图片等失败！请重试"+t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
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
        Intent intent = new Intent(UserInfoActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    private void showData(String data){
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
        setAvatar();//从服务器获取头像

        SharedPreferences sp=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        UpdateUserInfo.updateUser(sp);//将本地用户数据同步到服务器
    }
}
