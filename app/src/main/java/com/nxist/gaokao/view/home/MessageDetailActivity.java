package com.nxist.gaokao.view.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.Message;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageDetailActivity extends AppCompatActivity {
    private TextView messageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_message_detail_activity);
        initUI();
    }

    private void initUI(){
        messageContent=(TextView)findViewById(R.id.messageContent);
        Intent intent = getIntent();
        int messageId=intent.getIntExtra("messageId",-1);
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/getMessageContent?messageId="+messageId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("getActivity()", "这是什么？: "+response.body().toString());
                //使用Gson解析后台JSON字符串
                Gson gson=new Gson();
                try{
                    //构建topicDataList
                    Message message=gson.fromJson(response.body().toString(),Message.class);
                    Log.d("getActivity()", "这是什么异常？: ");
                    messageContent.setText(message.getContent());
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
}
