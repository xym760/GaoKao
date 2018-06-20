package com.nxist.gaokao.services;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nxist.gaokao.Module.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateUserInfo {
    private static Boolean updateStatus;
    /**
     * 更新服务器用户数据
     * @return 返回true表明更新成功
     */
    public static Boolean updateUser(SharedPreferences sp){
        User userData=new User();
        userData.setUserAccount(sp.getString("account",""));
        userData.setPassword(sp.getString("password",""));
        userData.setUserName(sp.getString("name",""));
        userData.setSubject(sp.getString("subject",""));
        userData.setProvince(sp.getString("province",""));
        userData.setScore(sp.getInt("score",0));
        userData.setAvatar(sp.getString("avatar",""));
        Gson gson=new Gson();
        String userString=gson.toJson(userData);
        NetworkConnect.postData("user/update", userString, new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.body().toString().equals("-1.0"))
                    updateStatus=false;
                else{//修改成功
                    updateStatus=true;
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                updateStatus=false;
            }

        });
        return updateStatus;
    }
}
