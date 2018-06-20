package com.nxist.gaokao.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.TopicAdapter;
import com.nxist.gaokao.Module.TopicData;
import com.nxist.gaokao.Module.User;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.UserService;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * 考友圈
 * Created by 徐源茂 on 2018/3/18.
 */

public class ExchangeFragment extends Fragment  implements View.OnClickListener ,GradationScrollView.ScrollViewListener {
    private RoundedImageView userIcon;//顶部用户头像
    private ImageView backGroundImg;//顶部背景图
    private GradationScrollView scrollView;//整个滚动视图
    private RelativeLayout spaceTopChange;//顶部按钮所在布局
    private int height;
    private TopicAdapter adapter;
    private List<TopicData> topicDataList;//存放数据列表;
    private NoScrollListView spaceList;//底部List

    public LayoutInflater inflater;
    public View loadmoreView;//加载视图
    public boolean isLoading = false;//表示是否正处于加载状态

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.exchange,container,false);
        intiView(view);
        initListeners();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 初始化控件
     */
    private void intiView(View view) {
        view.findViewById(R.id.spaceAdd).setOnClickListener(this);
        view.findViewById(R.id.spaceBack).setOnClickListener(this);
        spaceList= (NoScrollListView)view.findViewById(R.id.spaceList);

        inflater = LayoutInflater.from(getActivity().getApplicationContext());
        loadmoreView = inflater.inflate(R.layout.load_more, null);//获得刷新视图
        loadmoreView.setVisibility(View.VISIBLE);//设置刷新视图默认情况下是不可见的
        spaceList.addFooterView(loadmoreView,null,false);

        backGroundImg= (ImageView)view.findViewById(R.id.backGroundImg);
        backGroundImg.setFocusable(true);//用于在触摸模式和键盘模式（使用上/下/下一个键）上启用/禁用视图的焦点事件
        backGroundImg.setFocusableInTouchMode(true);//主要用于在触摸模式下启用/禁用视图的焦点事件
        backGroundImg.requestFocus();
        userIcon=(RoundedImageView)view.findViewById(R.id.user_icon);


        scrollView = (GradationScrollView) view.findViewById(R.id.scrollview);
        spaceTopChange= (RelativeLayout) view.findViewById(R.id.spaceTopChange);
        adapter=new TopicAdapter(getActivity().getApplicationContext(),topicDataList);
        spaceList.setAdapter(adapter);
    }

    /**
     * 更新头像
     */
    private void updateAvatar(){
        SharedPreferences sp=getActivity().getApplicationContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if(sp.getString("avatar","").equals("")){//用户服务器端未设置头像

        }else{
            String urlImg= BasicData.USER_AVATAR_URL+sp.getInt("userId",0)+"_"+sp.getString("avatar","");
            Picasso.with(getActivity().getApplicationContext()).load(urlImg).placeholder(R.drawable.ic_help).into(userIcon);
        }
    }


    /**
     * 查询数据
     */
    private void getData() {

        SharedPreferences sharedPreferences=getContext().getSharedPreferences("userinfo",MODE_PRIVATE);
        //从服务器查询所有话题
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "exchange/getTopicData?userAccount="+sharedPreferences.getString("account",""), new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("getActivity()", "返回结构: "+response.body().toString());
                //使用Gson解析后台JSON字符串
                Gson gson=new Gson();
                try{
                    //构建topicDataList
                    topicDataList=gson.fromJson(response.body().toString(),new TypeToken<List<TopicData>>(){}.getType());
                    List<String> ninePicture;
                    int tdlSize=topicDataList.size();
                    for(int i=0;i<tdlSize;i++){Log.d("getActivity()", "返回like: "+topicDataList.get(i).getIsLike());
                        if(topicDataList.get(i).getStringPictureList()!=null){
                            ninePicture= Arrays.asList(topicDataList.get(i).getStringPictureList().split(","));
                            //添加图片位置修饰
                            int npSize=ninePicture.size();
                            for(int j=0;j<npSize;j++){
                                ninePicture.set(j,BasicData.SERVER_ADDRESS+"exchange/getPicture/?picture="+topicDataList.get(i).getTopicId()+"_"+ninePicture.get(j));
                            }
                            topicDataList.get(i).setPictureList(ninePicture);
                            if(ninePicture.size()>0)
                                topicDataList.get(i).setIsHavePicture(true);
                        }else
                            topicDataList.get(i).setIsHavePicture(false);
                    }
                    adapter.addOrder(topicDataList);
                    adapter.notifyDataSetChanged();
                    updateAvatar();//更新头像
                }
                catch (Exception e){//发生了JSON解析错误等其它异常
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.spaceAdd:
                if(!UserService.IsLogin(view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
                    Intent loginIntent=new Intent(getContext(),LoginActivity.class);
                    startActivity(loginIntent);
                    break;
                }
                startActivity(new Intent(getActivity().getApplicationContext(),PublishActivity.class));
                break;
            case R.id.spaceBack:
                getActivity().finish();
                break;
        }
    }
    /**
     * 获取顶部图片高度后，设置滚动监听
     */
    private void initListeners() {
        //spaceList.setOnScrollListener(this);
        //注册监听视图树的观察者(observer)，在视图树种全局事件改变时得到通知。这个全局事件不仅还包括整个树的布局，从绘画过程开始，触摸模式的改变等
        ViewTreeObserver vto = backGroundImg.getViewTreeObserver();
        /**
         * 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数
         * 参数 listener    将要被添加的回调函数
         * 异常 IllegalStateException       如果isAlive() 返回false
         */
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                spaceTopChange.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                height = backGroundImg.getHeight();

                scrollView.setScrollViewListener(ExchangeFragment.this);
            }
        });
    }

    /**
     * 滑动监听
     * 根据滑动的距离动态改变标题栏颜色
     */
    @Override
    public void onScrollChanged(GradationScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {   //设置标题的背景颜色
            spaceTopChange.setBackgroundColor(Color.argb( 0, 144, 151, 166));
        } else if (y > 0 && y <= height-10) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
            float scale = (float) y / height;
            float alpha = (255 * scale);
            spaceTopChange.setBackgroundColor(Color.argb((int) alpha, 130, 117, 140));
        } else {    //滑动到banner下面设置普通颜色
            spaceTopChange.setBackgroundColor(Color.parseColor("#584f60"));
        }
    }

    /**
     * 滑动监听
     * 滑动到底部
     */
    @Override
    public void onScrollBottom(GradationScrollView scrollView) {
        //表示此时需要显示刷新视图界面进行新数据的加载(要等滑动停止)
        if(!isLoading)
        {
            //不处于加载状态的话对其进行加载
            isLoading = true;
            //设置刷新界面可见
            loadmoreView.setVisibility(View.VISIBLE);//设置刷新视图默认情况下是不可见的
            onLoad();
        }
    }

    /**
     * 刷新加载
     */
    public void onLoad()
    {
        if(adapter == null)
        {
            adapter = new TopicAdapter(getActivity().getApplicationContext(), topicDataList);
            spaceList.setAdapter(adapter);
        }else
        {
            //getData();
        }
        isLoading = false;//设置正在刷新标志位false
        //loadComplete();//刷新结束
    }

    /**
     * 加载完成
     */
    public void loadComplete()
    {
        getActivity().invalidateOptionsMenu();
        spaceList.removeFooterView(loadmoreView);//如果是最后一页的话，则将其从ListView中移出
    }
}
