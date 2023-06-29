package com.nxist.gaokao.view.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.nxist.gaokao.R;
import com.nxist.gaokao.services.UserService;
import com.nxist.gaokao.view.LoginActivity;

public class CollectActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private Button collectSchool;
    private Button collectMajor;
    private Button collectTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pc_collect_activity);
        if(!UserService.IsLogin(getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
            Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        initUI();
    }

    private void initUI(){
        imageView1=(ImageView)findViewById(R.id.imageView_school);
        imageView2=(ImageView)findViewById(R.id.imageView_major);
        imageView3=(ImageView)findViewById(R.id.imageView_topic);
        collectSchool=(Button)findViewById(R.id.collect_school);
        collectSchool.setOnClickListener(this);
        collectMajor=(Button)findViewById(R.id.collect_major);
        collectMajor.setOnClickListener(this);
        collectTopic=(Button)findViewById(R.id.collect_topic);
        collectTopic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.collect_school:
                Intent schoolIntent=new Intent(CollectActivity.this,CollectCollege.class);
                startActivity(schoolIntent);
                break;
            case R.id.collect_major:
                Intent majorIntent=new Intent(CollectActivity.this,CollectMajor.class);
                startActivity(majorIntent);
                break;
            case R.id.collect_topic:
                break;
        }
    }
}
