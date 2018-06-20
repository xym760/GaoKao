package com.nxist.gaokao.view.exchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText commentContent;
    private Button publishTopic;
    private Button cancelButton;
    int topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex_comment_activity);
        initUI();
    }

    private void initUI(){
        commentContent=(EditText)findViewById(R.id.comment_content);
        publishTopic=(Button)findViewById(R.id.publish_comment);
        publishTopic.setOnClickListener(this);
        cancelButton=(Button)findViewById(R.id.cancel_comment);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.publish_comment:
                String commentContentString=commentContent.getText().toString();
                Intent intent = getIntent();
                topicId=intent.getIntExtra("topicId",-1);
                Log.d("v.getContext()", "点击topicId: "+topicId);
                SharedPreferences sharedPreferences=v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE);
                int userId=sharedPreferences.getInt("userId", -1);
                NetworkConnect.getData(BasicData.SERVER_ADDRESS, "exchange/topicComment?userId="+userId+"&topicId="+topicId+"&replyContent="+commentContentString, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
                break;
            case R.id.cancel_comment:
                finish();
                break;
            default:break;
        }
    }
}
