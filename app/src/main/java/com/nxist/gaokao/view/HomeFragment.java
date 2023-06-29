package com.nxist.gaokao.view;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.Message;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.view.home.MessageDetailActivity;
import com.nxist.gaokao.view.home.RecommendCollege;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

import androidx.fragment.app.Fragment;

/**
 * Created by 徐源茂 on 2018/3/18.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
    private Button schoolQuery;
    private Button majorQuery;
    private Button characterTest;
    private TextView countDown;
    private SliderLayout mDemoSlider;
    private List<Message> messageDataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.home,container,false);
        initUI(view);
        return view;
    }

    private void initUI(View view){
        countDown=(TextView)view.findViewById(R.id.countDown);
        setCountDown();
        schoolQuery=(Button)view.findViewById(R.id.school_query);
        schoolQuery.setOnClickListener(this);
        majorQuery=(Button)view.findViewById(R.id.major_query);
        majorQuery.setOnClickListener(this);
        characterTest=(Button)view.findViewById(R.id.characterTest);
        characterTest.setOnClickListener(this);
        //滚动推荐
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);


        HashMap<String,String> urlMaps = new HashMap<>();
        Log.d("getActivity()", "tp地址:");
        TextSliderView textSliderView = new TextSliderView(getActivity().getApplicationContext());
        textSliderView
                .description("加载中")//描述
                .image(R.drawable.ic_default_image)//image方法可以传入图片url、资源id、File
                .setScaleType(BaseSliderView.ScaleType.Fit)//图片缩放类型
                .setOnSliderClickListener(onSliderClickListener);//图片点击
        textSliderView.bundle(new Bundle());
        textSliderView.getBundle().putInt("extra",-1);//传入参数
        mDemoSlider.addSlider(textSliderView);//添加一个滑动页面

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);//滑动动画
        // mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);//默认指示器样式
        mDemoSlider.setCustomIndicator((PagerIndicator)view.findViewById(R.id.custom_indicator2));//自定义指示器
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());//设置图片描述显示动画
        mDemoSlider.setDuration(4000);//设置滚动时间，也是计时器时间
        mDemoSlider.addOnPageChangeListener(onPageChangeListener);

        getData(view);

    }

    //Banner的Item点击监听，也就是ViewPager的某一页点击监听。
    private BaseSliderView.OnSliderClickListener onSliderClickListener=new BaseSliderView.OnSliderClickListener() {
        @Override
        public void onSliderClick(BaseSliderView slider) {
            Intent intent=new Intent(getContext(), MessageDetailActivity.class);
            intent.putExtra("messageId",Integer.parseInt(slider.getBundle().get("extra").toString()));
            startActivity(intent);
        }
    };

    //页面改变监听,ViewPager是我们经常用到的控件
    private ViewPagerEx.OnPageChangeListener onPageChangeListener=new ViewPagerEx.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            Log.d("ansen", "Page Changed: " + position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.major_query:
                Intent intentMajor=new Intent(getActivity().getApplicationContext(),MajorQuery.class);
                startActivity(intentMajor);
                break;
            case R.id.school_query:
                Intent intentSchool=new Intent(getActivity().getApplicationContext(),CollegeQuery.class);
                startActivity(intentSchool);
                break;
            case R.id.characterTest:
                Intent intentRecommendCollege=new Intent(getActivity().getApplicationContext(), RecommendCollege.class);
                startActivity(intentRecommendCollege);
                break;
            default:
                    break;
        }
    }

    //高考倒计时
    private void setCountDown(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String first = "2018-6-6";
        try{
            Date firstdate = format.parse(first);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(format.format(new Date())));
            int cnt = 0;
            while(calendar.getTime().compareTo(firstdate)!=0){
                calendar.add(Calendar.DATE, 1);
                cnt++;
                if(cnt==365)
                    break;
            }
            countDown.setText("距离高考还有"+cnt+"天");
        }
        catch (Exception e){
            Log.e("getContext()","首页异常：");
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void getData(final View view) {
        //从服务器查询所有消息
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/getMessageData", new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("getActivity()", "返回结构: "+response.body().toString());
                //使用Gson解析后台JSON字符串
                Gson gson=new Gson();
                try{
                    //构建topicDataList
                    messageDataList=gson.fromJson(response.body().toString(),new TypeToken<List<Message>>(){}.getType());
                    HashMap<String,String> urlMaps = new HashMap<>();
                    mDemoSlider.removeAllSliders();
                    for(Message name : messageDataList){
                        Log.d("getActivity()", "tp地址:"+urlMaps.get(name));
                        TextSliderView textSliderView = new TextSliderView(getActivity().getApplicationContext());
                        textSliderView
                                .description(name.getTitle())//描述
                                .image(BasicData.SERVER_ADDRESS+"home/returnMessagePicture?picture="+name.getMessageId()+"_"+name.getPicture())//image方法可以传入图片url、资源id、File
                                .setScaleType(BaseSliderView.ScaleType.Fit)//图片缩放类型
                                .setOnSliderClickListener(onSliderClickListener);//图片点击
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle().putInt("extra",name.getMessageId());//传入参数
                        mDemoSlider.addSlider(textSliderView);//添加一个滑动页面
                    }

                    mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);//滑动动画
                    // mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);//默认指示器样式
                    mDemoSlider.setCustomIndicator((PagerIndicator)view.findViewById(R.id.custom_indicator2));//自定义指示器
                    mDemoSlider.setCustomAnimation(new DescriptionAnimation());//设置图片描述显示动画
                    mDemoSlider.setDuration(4000);//设置滚动时间，也是计时器时间
                    mDemoSlider.addOnPageChangeListener(onPageChangeListener);
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
