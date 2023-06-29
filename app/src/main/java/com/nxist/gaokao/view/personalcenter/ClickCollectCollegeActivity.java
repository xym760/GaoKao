package com.nxist.gaokao.view.personalcenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.College;
import com.nxist.gaokao.Module.CollegeAdapter;
import com.nxist.gaokao.Module.CollegeDetailItem;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.AndroidShare;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.SchoolJsonAnalysis;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClickCollectCollegeActivity extends AppCompatActivity implements View.OnClickListener {
    private List<CollegeDetailItem> CDIList=new ArrayList<>();//存放单一院校信息
    RecyclerView recyclerView;
    private ImageView pcCollectImageView;//关注
    private Boolean isCollect=false;//设置关注标记
    private String province;
    private String collegeName;
    private int collegeId;
    private ImageView pc_share_image;//分享

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pc_click_collect_college_activity);
        initUI();
    }

    private void initUI(){
        pcCollectImageView=(ImageView)findViewById(R.id.pc_collect_college_image);
        pcCollectImageView.setOnClickListener(this);
        pc_share_image=(ImageView)findViewById(R.id.pc_share_image);
        pc_share_image.setOnClickListener(this);
        Intent intent = getIntent();
        collegeName=intent.getStringExtra("collegeName");
        province=intent.getStringExtra("province");
        collegeId=intent.getIntExtra("collegeId",-1);
        //初始化List
        recyclerView=(RecyclerView)findViewById(R.id.SpecificCollegeDetails);
        recyclerView.addItemDecoration(new DividerItemDecoration(ClickCollectCollegeActivity.this, DividerItemDecoration.VERTICAL));
        //定义加载提示框
        final SweetAlertDialog pDialog = new SweetAlertDialog(ClickCollectCollegeActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载中...");
        pDialog.setCancelable(false);
        pDialog.show();
        //通过API接口查询学校
        NetworkConnect.getData("http://api.dayuapi.com/",
                "http://api.dayuapi.com/college?province="+province+"&name="+collegeName+"&appkey=a9e80a015d1df96bbc4f140d7f0a6a4a",
                new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try{
                            final College college= SchoolJsonAnalysis.getCollegeObject(SchoolJsonAnalysis.getSchool(response.body()));
                            initList(college);//将查询结果放入list
                            pDialog.hide();
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
        updateCollect();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.pc_collect_college_image://点击收藏
                Log.e("SchoolQuery.this","是否点击："+isCollect);
                SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
                int userId=sp.getInt("userId",-1);
                if(isCollect==false){//进行关注操作
                    NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeCollect?userId="+userId+"&collegeId="+collegeId, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("SchoolQuery.this","成功关注："+response.body());
                            updateCollect();
                            Toast.makeText(ClickCollectCollegeActivity.this,"关注成功！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                else{//取消关注
                    NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeCancelCollect?userId="+userId+"&collegeId="+collegeId, new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("SchoolQuery.this","取消关注："+response.body());
                            updateCollect();
                            Toast.makeText(ClickCollectCollegeActivity.this,"已取消关注！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
                break;
            case R.id.pc_share_image:
                //获取当前Activity截图
                final Bitmap bitmap=captureScreen(ClickCollectCollegeActivity.this);
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
                                AndroidShare androidShare=new AndroidShare(ClickCollectCollegeActivity.this);
                                if(result.equals("QQ")){
                                    androidShare.sharedQQ(ClickCollectCollegeActivity.this,bitmap);
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
        CDIList.removeAll(CDIList);
        CollegeDetailItem cName=new CollegeDetailItem("院校名称：",college.getName());
        CDIList.add(cName);
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
        Log.e("getContext()","简介："+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }

    private void updateCollect(){
        //查看该用户否关注该院校
        SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
        int userId=sp.getInt("userId",-1);
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/collegeIsCollect?userId="+userId+"&collegeId="+collegeId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("SchoolQuery.this","是否关注："+response.body());
                if(response.body().equals("0"))
                    isCollect=false;
                else
                    isCollect=true;
                Log.e("SchoolQuery.this","更新关注："+isCollect);
                if(isCollect)
                    pcCollectImageView.setImageResource(R.drawable.ic_favorites_filling);
                else
                    pcCollectImageView.setImageResource(R.drawable.ic_favorite);
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
