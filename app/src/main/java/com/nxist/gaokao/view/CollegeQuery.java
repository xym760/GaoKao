package com.nxist.gaokao.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.College;
import com.nxist.gaokao.Module.CollegeAdapter;
import com.nxist.gaokao.Module.CollegeDetailItem;
import com.nxist.gaokao.Module.CollegeItem;
import com.nxist.gaokao.Module.CollegeItemAdapter;
import com.nxist.gaokao.Module.NetworkAPI;
import com.nxist.gaokao.services.AndroidShare;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.SchoolJsonAnalysis;
import com.nxist.gaokao.services.UpdateUserInfo;
import com.nxist.gaokao.services.UserService;
import com.nxist.gaokao.view.personalcenter.ClickCollectCollegeActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CollegeQuery extends AppCompatActivity implements View.OnClickListener{
    private EditText provinceName;
    private EditText schoolName;
    private Button comfirm;
    private List<CollegeDetailItem> CDIList=new ArrayList<>();//存放单一院校信息
    private List<CollegeItem> CIList=new ArrayList<>();//存放多个院校
    RecyclerView recyclerView;
    private LinearLayout collegeBottomBar;//底部布局
    private ImageView collectImageView;//关注
    private College collegeTemp;//定义院校对象，用于收藏等操作
    private Boolean isCollect=false;//设置关注标记
    private ImageView shareImageView;//分享
    private SweetAlertDialog tempDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_query);
        initUI();
    }
    private void initUI(){
        collegeBottomBar=(LinearLayout)findViewById(R.id.college_bottom_bar);
        collectImageView=(ImageView)findViewById(R.id.collect_image);
        collectImageView.setOnClickListener(this);
        shareImageView=(ImageView)findViewById(R.id.share_image);
        shareImageView.setOnClickListener(this);
        collegeBottomBar.setVisibility(View.GONE);
        provinceName=(EditText)findViewById(R.id.provinceName);
        provinceName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){//得到焦点
                    //定义弹出适配器
                    ArrayAdapter<String> regionAdapter=new ArrayAdapter<String>(CollegeQuery.this,android.R.layout.simple_list_item_1,BasicData.PROVINCE);
                    //利用DialogPlus弹出对话框
                    DialogPlus dialog = DialogPlus.newDialog(CollegeQuery.this)
                            .setAdapter(regionAdapter)//设置适配器
                            .setGravity(Gravity.CENTER)//设置位置
                            .setCancelable(true)//设为可取消
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                    provinceName.setText(BasicData.PROVINCE[position]);
                                    dialog.dismiss();
                                }
                            })
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .create();
                    dialog.show();
                }else{//失去焦点

                }
            }
        });
        schoolName=(EditText)findViewById(R.id.schoolName);
        comfirm=(Button)findViewById(R.id.confirm);
        comfirm.setOnClickListener(this);
        //初始化List
        recyclerView=(RecyclerView)findViewById(R.id.collegeDetails);
        recyclerView.addItemDecoration(new DividerItemDecoration(CollegeQuery.this, DividerItemDecoration.VERTICAL));
        //查询数据库中所有院校
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeQuery", new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                initListNoConfirm(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.confirm://点击查询
                //设置底部栏可见
                collegeBottomBar.setVisibility(View.VISIBLE);
                collegeTemp=null;//清空上次查询的院校数据
                //定义加载提示框
                final SweetAlertDialog pDialog = new SweetAlertDialog(CollegeQuery.this, SweetAlertDialog.PROGRESS_TYPE);
                tempDialog=pDialog;
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("加载中...");
                pDialog.setCancelable(false);
                pDialog.show();
                String provinceNameContent=provinceName.getText().toString();
                String collegeNameContent=schoolName.getText().toString();
                //通过API接口查询学校
                NetworkConnect.getData("http://api.dayuapi.com/",
                        "http://api.dayuapi.com/college?province="+provinceNameContent+"&name="+collegeNameContent+"&appkey=a9e80a015d1df96bbc4f140d7f0a6a4a",
                        new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try{
                                    //Toast.makeText(SchoolQuery.this,"学校地址是："+SchoolJsonAnalysis.getSchool(response.body()).getString("addr"),Toast.LENGTH_SHORT).show();
                                    final College college=SchoolJsonAnalysis.getCollegeObject(SchoolJsonAnalysis.getSchool(response.body()));
                                    initList(college);//将查询结果放入list
                                    pDialog.hide();
                                    //将查询到的学校数据存入服务器
                                    Gson gson=new Gson();
                                    String collegeString=gson.toJson(college);
                                    Retrofit retrofit=new Retrofit.Builder()
                                            .baseUrl(BasicData.SERVER_ADDRESS)
                                            .addConverterFactory( GsonConverterFactory.create())
                                            .build();
                                    NetworkAPI networkAPI=retrofit.create(NetworkAPI.class);
                                    RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8")
                                            ,collegeString);
                                    Log.d("SchoolQuery.this", "collegeString:"+collegeString);
                                    Call<College> call1=networkAPI.postCollege(body);
                                    call1.enqueue(new Callback<College>() {
                                        @Override
                                        public void onResponse(Call<College> call, Response<College> response) {
                                            Log.e("SchoolQuery.this","成功："+response.body());
                                            collegeTemp=response.body();
                                            updateCollect();
                                        }

                                        @Override
                                        public void onFailure(Call<College> call, Throwable t) {
                                            Log.e("SchoolQuery.this",t.getMessage()+"错误444");
                                        }
                                    });
                                }catch (Exception e){
                                    pDialog.hide();
                                    e.printStackTrace();
                                }

                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                pDialog.hide();
                            }
                        });
                break;

            case R.id.collect_image://点击收藏
                if(!UserService.IsLogin(getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
                    Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(loginIntent);
                    break;
                }
                Log.e("SchoolQuery.this","是否点击："+isCollect);
                SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
                int collegeIdCollect=collegeTemp.getCollegeId();
                int userId=sp.getInt("userId",-1);
                if(isCollect==false){//进行关注操作
                    NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeCollect?userId="+userId+"&collegeId="+collegeIdCollect, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("SchoolQuery.this","成功关注："+response.body());
                            updateCollect();
                            Toast.makeText(CollegeQuery.this,"关注成功！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                else{//取消关注
                    NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeCancelCollect?userId="+userId+"&collegeId="+collegeIdCollect, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("SchoolQuery.this","取消关注："+response.body());
                            updateCollect();
                            Toast.makeText(CollegeQuery.this,"已取消关注！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                break;
            case R.id.share_image://点击分享按钮
                //获取当前Activity截图
                final Bitmap bitmap=captureScreen(CollegeQuery.this);
                //定义弹出适配器
                ArrayAdapter<String> shareAdapter=new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_list_item_1,BasicData.SHARE_ITEM);
                //利用DialogPlus弹出对话框
                DialogPlus dialog = DialogPlus.newDialog(v.getContext())
                        .setAdapter(shareAdapter)//设置适配器
                        .setGravity(Gravity.BOTTOM)//设置位置
                        .setCancelable(true)//设为可取消
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                //得到用户选择的分享项
                                String result=BasicData.SHARE_ITEM[position];
                                AndroidShare androidShare=new AndroidShare(CollegeQuery.this);
                                if(result.equals("QQ")){
                                    androidShare.sharedQQ(CollegeQuery.this,bitmap);
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
    //初始化列表数据
    private void initList(College college){
        Log.d("SchoolQuery.this", "院校名称：：:"+college.getName());
        CDIList.removeAll(CDIList);
        CollegeDetailItem cName=new CollegeDetailItem("院校名称：",college.getName());
        CDIList.add(cName);Log.d("ShcoolQuery.this", "CDIList.size()1:"+college.getName());
        CollegeDetailItem cFormerName=new CollegeDetailItem("曾用名：",college.getFormerName());
        CDIList.add(cFormerName);
        CollegeDetailItem cProvince=new CollegeDetailItem("所在省份：",college.getProvince());
        CDIList.add(cProvince);
        CollegeDetailItem cGrade=new CollegeDetailItem("院校类型：",college.getGrade());
        CDIList.add(cGrade);
        CollegeDetailItem  cProperty=new CollegeDetailItem("院校属性：",college.getProperty());
        CDIList.add(cProperty);
        CollegeDetailItem cDirectlyUnder;
        if(college.getDirectlyUnder()==true)
            cDirectlyUnder=new CollegeDetailItem("是否教育部直属：","是");
        else
            cDirectlyUnder=new CollegeDetailItem("是否教育部直属：","否");
        CDIList.add(cDirectlyUnder);
        CollegeDetailItem cRunNature=new CollegeDetailItem("办学性质：",college.getRunNature());
        CDIList.add(cRunNature);
        CollegeDetailItem cRanking=new CollegeDetailItem("院校排名：",String.valueOf(college.getRanking()));
        CDIList.add(cRanking);
        CollegeDetailItem cWebsite=new CollegeDetailItem("院校官网：",college.getWebsite());
        CDIList.add(cWebsite);
        CollegeDetailItem cTelephone=new CollegeDetailItem("招办电话：",college.getTelephone());
        CDIList.add(cTelephone);
        CollegeDetailItem cAddress=new CollegeDetailItem("详细地址:",college.getAddress());
        CDIList.add(cAddress);
        CollegeDetailItem cMailBox=new CollegeDetailItem("邮箱：",college.getMailbox());
        CDIList.add(cMailBox);
        CollegeDetailItem cIntro=new CollegeDetailItem("简介：",college.getIntro());
        CDIList.add(cIntro);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        CollegeAdapter adapter=new CollegeAdapter(CDIList);
        recyclerView.setAdapter(adapter);
    }
    //初始化列表数据（用户未点击确认按钮）
    private void initListNoConfirm(String jsonString){
        CIList.removeAll(CIList);//清空List
        //使用Gson解析后台JSON字符串
        Gson gson=new Gson();
        CIList=gson.fromJson(jsonString,new TypeToken<List<CollegeItem>>(){}.getType());
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        CollegeItemAdapter adapter=new CollegeItemAdapter(CIList,provinceName,schoolName);
        recyclerView.setAdapter(adapter);
    }
    private void updateCollect(){
        //查看该用户否关注该院校
        SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
        int collegeIdCollect=collegeTemp.getCollegeId();
        int userId=sp.getInt("userId",-1);
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeIsCollect?userId="+userId+"&collegeId="+collegeIdCollect, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("SchoolQuery.this","是否关注："+response.body());
                if(response.body().equals("0"))
                    isCollect=false;
                else
                    isCollect=true;
                Log.e("SchoolQuery.this","更新关注："+isCollect);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tempDialog != null) {
            tempDialog.dismiss();
        }
    }
}
