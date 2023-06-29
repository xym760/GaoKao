package com.nxist.gaokao.view.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.R;

public class RecommendCollege extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_college_activity);
        webView = (WebView) findViewById(R.id.recommendCollege);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadData("","text/html","UTF-8");
        SharedPreferences sharedPreferences=getSharedPreferences("userinfo",MODE_PRIVATE);
        int score=sharedPreferences.getInt("score",0);
        String subject=sharedPreferences.getString("subject","");
        String province=sharedPreferences.getString("province","");
        webView.loadUrl(BasicData.SERVER_ADDRESS+"home/recommendCollegetoAndroid?score="+score+"&province="+province+"&subject="+subject);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
