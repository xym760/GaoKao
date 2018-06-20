package com.nxist.gaokao.services;

import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.NetworkAPI;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by xym760 on 2018/4/4.
 */

public class UploadMethod {
    private static Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 30;//超时时长，单位：秒

    /**
     * 初始化 Retrofit
     */
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .client(okHttpBuilder.build())
                    .baseUrl(BasicData.SERVER_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * 上传多张图片
     * @param suffixUrl 服务器映射地址后缀
     * @param otherInfoInHeader 要在上传文件名前添加的内容
     * @param pathList 图片文件列表
     * @param callback 回调
     */
    public static void upLoadPicture(String suffixUrl,String otherInfoInHeader,List<String> pathList,final Callback<ResponseBody> callback) {
        //创建builder
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型
        //将多张图片放入builder中
        for (int i = 0; i < pathList.size(); i++) {
            File file = new File(pathList.get(i));//filePath 图片地址
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            /**
             * "imgfile"+i 后台接收图片流的参数名
             * otherInfoInHeader+file.getName() :文件名称
             */
            builder.addFormDataPart("imgfile"+i, otherInfoInHeader+file.getName(), imageBody);
        }
        //创建列表parts
        List<MultipartBody.Part> parts = builder.build().parts();
        //创建接口
        NetworkAPI networkAPI=getRetrofit().create(NetworkAPI.class);
        //创建回调
        Call<ResponseBody> call=networkAPI.uploadPicture(suffixUrl,parts);
        call.enqueue(new Callback<ResponseBody>() {//返回结果
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //调用回调
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //调用回调
                callback.onFailure(call, t);
            }
        });
    }
}
