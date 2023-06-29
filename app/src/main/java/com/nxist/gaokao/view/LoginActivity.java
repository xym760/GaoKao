package com.nxist.gaokao.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nxist.gaokao.Module.User;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.UserService;
import com.nxist.gaokao.view.personalcenter.RegisterActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText pcLoginUserName;
    private EditText pcLoginPassword;
    private CheckBox pcLoginRemember;
    private Button pcLoginButton;
    private Button pcLoginRegister;
    private SweetAlertDialog tempDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pc_login_activity);
        initUI();
    }

    private void initUI(){
        pcLoginUserName=(EditText)findViewById(R.id.pcLoginUserName);
        pcLoginPassword=(EditText)findViewById(R.id.pcLoginPassword);
        pcLoginRemember=(CheckBox)findViewById(R.id.pcLoginRemember);
        SharedPreferences spPassword=getSharedPreferences("rememberPassword",MODE_PRIVATE);
        if(spPassword.getBoolean("isRemembered",false)){
            pcLoginUserName.setText(spPassword.getString("account",null));
            pcLoginPassword.setText(spPassword.getString("password",null));
            pcLoginRemember.setChecked(true);
        }
        pcLoginButton=(Button)findViewById(R.id.pc_login_button);
        pcLoginButton.setOnClickListener(this);
        pcLoginRegister=(Button)findViewById(R.id.pc_register);
        pcLoginRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.pc_login_button://点击登录
                String userAccount=pcLoginUserName.getText().toString();
                String password=pcLoginPassword.getText().toString();
                //各种登录验证
                if(userAccount.equals("")){
                    Toast.makeText(LoginActivity.this,"请输入用户名！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.equals("")){
                    Toast.makeText(LoginActivity.this,"请输入密码！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(pcLoginRemember.isChecked()){//如果勾选记住密码
                    SharedPreferences.Editor editorPassword=getSharedPreferences("rememberPassword",MODE_PRIVATE).edit();
                    editorPassword.putString("account",userAccount);//用户账户
                    editorPassword.putString("password",password);//密码
                    editorPassword.putBoolean("isRemembered",true);
                    editorPassword.apply();
                    Log.d("getActivity()", "记住密码: ");
                }else {
                    UserService.deleteSharedPreferences(getSharedPreferences("rememberPassword", Context.MODE_PRIVATE));
                }

                //定义加载提示框
                SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                tempDialog=pDialog;
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("登录中...");
                pDialog.setCancelable(false);
                pDialog.show();

                User user=new User();
                user.setUserAccount(userAccount);
                user.setPassword(password);
                Gson gson=new Gson();
                String userString=gson.toJson(user);
                NetworkConnect.postData("user/login", userString, new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.body()==null)
                            Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                        else{
                            Log.d("LoginActivity.this", "登录返回字符串:"+response.body());
                            //使用Gson将JSON对象字符串解析为对象
                            Gson userJson=new Gson();
                            User returnUser=userJson.fromJson(response.body().toString(),User.class);Log.d("LoginActivity.this", "登录返回字符串:解析");
                            //保存用户数据到本地
                            SharedPreferences.Editor editor=getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                            editor.putInt("userId",returnUser.getUserId());//账户Id
                            editor.putString("account",returnUser.getUserAccount());//用户账户
                            editor.putString("password",returnUser.getPassword());//密码
                            editor.putString("name",returnUser.getUserName());//姓名
                            editor.putString("subject",returnUser.getSubject());//文科还是理科
                            editor.putString("province",returnUser.getProvince());//省
                            editor.putString("avatar",returnUser.getAvatar());//头像
                            if(returnUser.getScore()!=null)
                                editor.putInt("score",returnUser.getScore());//分数
                            else
                                editor.putInt("score",-1);//如果用户没有输入分数，则设为-1
                            editor.apply();
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            tempDialog.dismiss();
                            finish();
                        }
                        tempDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,"登录失败,因为"+t.getMessage(),Toast.LENGTH_SHORT).show();
                        tempDialog.dismiss();
                    }

                });
                break;
            case R.id.pc_register://点击注册
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tempDialog != null) {
            tempDialog.dismiss();
        }
    }
}
