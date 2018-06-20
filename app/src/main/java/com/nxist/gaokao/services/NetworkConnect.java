package com.nxist.gaokao.services;


import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.College;
import com.nxist.gaokao.Module.NetworkAPI;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 徐源茂 on 2018/3/24.
 */

public class NetworkConnect {
    /**
     * @param baseUrl 如http://api.dayuapi.com/,这里有坑，最后后缀出带着“/”
     * @param url 如http://api.dayuapi.com/college?province=宁夏&name=宁夏理工学院&appkey=a9e80a015d1df96bbc4f140d7f0a6a4a
     * @param callback  添加请求回调，这里直接使用的是Retrofit自身的回调接口
     * @return
     */
    public static void getData(String baseUrl,String url,final Callback<String> callback){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)//指定baseurl，这里有坑，最后后缀出带着“/”
                .addConverterFactory(ScalarsConverterFactory.create())//设置内容格式,这种对应的数据返回值是String类型
                .build();
        //通过retrofit和定义的有网络访问方法的接口关联
        NetworkAPI projectAPI = retrofit.create(NetworkAPI.class);
        //在这里又重新设定了一下地址，是因为Retrofit要求传入具体，如果是决定路径的话，路径会将baseUrl覆盖掉
        Call<String> call = projectAPI.getMethod(url);
        //执行异步请求
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //调用回调
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //调用回调
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * 上传JSON数据到服务器
     * @param suffixUrl 具体地址
     * @param postJSONString 要上传的JSON字符串
     * @param callback 回调
     */
    public static void postData(String suffixUrl,String postJSONString,final Callback<Object> callback){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BasicData.SERVER_ADDRESS)
                .addConverterFactory( GsonConverterFactory.create())
                .build();
        NetworkAPI networkAPI=retrofit.create(NetworkAPI.class);
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8")
                ,postJSONString);
        Call<Object> call=networkAPI.postMethod(suffixUrl,body);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                //调用回调
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                //调用回调
                callback.onFailure(call, t);
            }
        });
    }
}
