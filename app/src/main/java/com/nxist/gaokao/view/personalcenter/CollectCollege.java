package com.nxist.gaokao.view.personalcenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.CollectCollegeItemAdapter;
import com.nxist.gaokao.Module.CollegeItem;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectCollege extends AppCompatActivity {
    private List<CollegeItem> collectList=new ArrayList<>();//存放多个院校
    private EditText provinceName;
    private EditText schoolName;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pc_collect_college);
        initUI();
    }

    private void initUI(){
        //初始化List
        recyclerView=(RecyclerView)findViewById(R.id.collectCollegeDetails);
        recyclerView.addItemDecoration(new DividerItemDecoration(CollectCollege.this, DividerItemDecoration.VERTICAL));
        getCollectCollegeList();
    }

    //查询用户关注项
    private void getCollectCollegeList(){
        //查询数据库中用户关注的所有院校
        SharedPreferences sp=getSharedPreferences("userinfo",MODE_PRIVATE);
        int userId=sp.getInt("userId",-1);
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "personalcenter/collectCollege?userId="+userId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                initList(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    //更新列表数据
    private void initList(String jsonString){
        collectList.removeAll(collectList);//清空List
        //使用Gson解析后台JSON字符串
        Gson gson=new Gson();
        collectList=gson.fromJson(jsonString,new TypeToken<List<CollegeItem>>(){}.getType());
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        if(collectList.size()==0)
            collectList.add(new CollegeItem("暂无收藏",""));
        CollectCollegeItemAdapter adapter=new CollectCollegeItemAdapter(collectList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        getCollectCollegeList();
    }
}
