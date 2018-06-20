package com.nxist.gaokao.Module;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by 徐源茂 on 2018/3/24.
 * 封装一个简易的请求接口
 */

public interface NetworkAPI {
    //http://www.baidu.com/aaa?key=123&qq=aaa
    @GET
    Call<String> getMethod(@Url String url);

    /**
     * 上传JSON数据接口
     * @param url 上传具体地址
     * @param requestBody
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    /**
     * 上传JSON数据
     */
    @POST
    Call<Object> postMethod(@Url String url,@Body RequestBody requestBody);//传入的参数为RequestBody

    /**
     * 上传学校
     * @param requestBody
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("home/insertCollege")
    Call<College> postCollege(@Body RequestBody requestBody);//传入的参数为RequestBody

    /**
     * 上传图片
     */
    @Multipart
    @POST
    Call<ResponseBody> uploadPicture(@Url String url, @Part List<MultipartBody.Part> partList);
}
