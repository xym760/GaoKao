package com.nxist.gaokao.services;

import android.content.SharedPreferences;

public class UserService {
    /**
     * 判断用户是否登录
     * @param sp
     * @return true为已登录 false为未登录
     */
    public static Boolean IsLogin(SharedPreferences sp){
        String account=sp.getString("account","");
        String password=sp.getString("password","");
        if(account!=null&&!account.equals("")&&password!=null&&!password.equals("")){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 删除SharedPreferences数据
     * @param sp
     */
    public static void deleteSharedPreferences(SharedPreferences sp){
        sp.edit().clear().commit();
    }
}
