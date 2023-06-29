package com.nxist.gaokao.view.personalcenter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nxist.gaokao.Module.User;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    private EditText name;
    private EditText password;
    private EditText againPassword;
    private VerificationSeekBar verification;
    private TextView seekBarHint;
    private Button confirm;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pc_register);
        initUI();
    }

    private void initUI(){
        name=(EditText)findViewById(R.id.name);
        password=(EditText)findViewById(R.id.password);
        againPassword=(EditText)findViewById(R.id.again_password);
        seekBarHint=findViewById(R.id.seekbarhint);
        verification=(VerificationSeekBar)findViewById(R.id.verification);
        verification.setOnSeekBarChangeListener(this);
        confirm=(Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        reset=(Button)findViewById(R.id.reset);
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.confirm:
                String userAccount=name.getText().toString();
                String userPassword=password.getText().toString();
                String userAgainPassword=againPassword.getText().toString();
                if(!userPassword.equals(userAgainPassword)){
                    Toast.makeText(RegisterActivity.this,"两次输入的密码不一致，请重新输入密码！",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(verification.isEnabled()){
                    Toast.makeText(RegisterActivity.this,"请拖到滑块，完成验证，谢谢！",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(!userAccount.equals("")&&!password.equals("")){//如果用户名与密码不为空
                    //定义加载提示框
                    final SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("注册中...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    User user=new User();
                    user.setUserAccount(userAccount);
                    user.setPassword(userPassword);
                    Gson gson=new Gson();
                    String userString=gson.toJson(user);
                    NetworkConnect.postData("user/register", userString, new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.body().toString().equals("-1.0"))
                            Toast.makeText(RegisterActivity.this,"该用户已存在请登录！",Toast.LENGTH_SHORT).show();
                        else{
                            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            pDialog.hide();
                            finish();
                        }
                            pDialog.hide();
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this,"注册失败,因为"+t.getMessage(),Toast.LENGTH_LONG).show();
                            pDialog.hide();
                        }

                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this,"用户名与密码不能为空！",Toast.LENGTH_SHORT).show();
                    break;
                }
                break;
            case R.id.reset:
                name.setText("");
                password.setText("");
                againPassword.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getProgress() == seekBar.getMax()) {
            seekBarHint.setVisibility(View.VISIBLE);
            seekBarHint.setTextColor(Color.WHITE);
            seekBarHint.setText("完成验证");
        } else {
            seekBarHint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getProgress() != seekBar.getMax()) {
            seekBar.setProgress(0);
            seekBarHint.setVisibility(View.VISIBLE);
            seekBarHint.setTextColor(Color.GRAY);
            seekBarHint.setText("拖到最右边谢谢");
        }else {
            verification.setEnabled(false);
            //setData();
        }
    }
}
