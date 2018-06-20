package com.nxist.gaokao;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.nxist.gaokao.view.ExchangeFragment;
import com.nxist.gaokao.view.HomeFragment;
import com.nxist.gaokao.view.PersonalCenterFragment;


public class MainActivity extends AppCompatActivity {
    private Button searchSchoolButton;
    private EditText schoolNameEditText;
    private WebView webViewShow;
    private FragmentManager mFragmentManager;
    private Fragment homeFragment,exchangeFragment,personalCenterFragment,fragmentNow;
    private FrameLayout anotherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();//初始化界面
    }

    //定义初始化界面方法
    private void initUI(){
        //初始化Fragment
        anotherFragment=(FrameLayout) findViewById(R.id.another_fragment);
        //实例化所有Fragment
        homeFragment = new HomeFragment();
        exchangeFragment=new ExchangeFragment();
        personalCenterFragment=new PersonalCenterFragment();
        //获取碎片管理者
        mFragmentManager = getSupportFragmentManager();
        //开启一个事务
        FragmentTransaction fragmentTransaction=mFragmentManager.beginTransaction();
        //add：往碎片集合中添加一个碎片；
        //参数：1.公共父容器的的id  2.fragment的碎片，设置为第一Fragment为默认Fragment
        fragmentTransaction.add(R.id.another_fragment,homeFragment);
        fragmentNow=homeFragment;//定位当前显示的Fragment
        fragmentTransaction.commit();


        //初始化底部导航栏
        //实例化BottomNavigationBar控件
        final BottomNavigationBar mBottomNavigationBar= (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING).setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        //值得一提，模式跟背景的设置都要在添加tab前面，不然不会有效果。
        mBottomNavigationBar.setActiveColor(R.color.green);//选中颜色 图标和文字
        mBottomNavigationBar.setInActiveColor(R.color.white);//默认未选择颜色
        mBottomNavigationBar.setBarBackgroundColor(R.color.blue);//默认背景色
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_home,"首页"));
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_earth,"考友圈"));
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_account_filling,"个人中心1"))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成

        //设置lab点击事件
        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {//未选中 -> 选中
                if(position==0)
                    setFragment(0);
                if(position==1)//如果是考友圈则调用getMethod方法
                    setFragment(1);
                if(position==2){//个人中心
                    setFragment(2);
                }
            }

            @Override
            public void onTabUnselected(int position) {//选中 -> 未选中
                if (position==2){
                    //mBottomNavigationBar.selectTab(position);
                }

            }

            @Override
            public void onTabReselected(int position) {//选中 -> 选中

            }
        });
        //webview = (WebView) findViewById(R.id.webview1);
        //webview.getSettings().setJavaScriptEnabled(true);
        //webview.getSettings().setDefaultTextEncodingName("utf-8");
        //setDefaultFragment();
    }

    //设置fragment的加载
    private void setFragment(int i){
        FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
        switch (i){
            case 0://如果点击主页
                //判断homeFragment是否存在
                if(homeFragment.isAdded()){
                    //如果homeFragment已经存在，则隐藏当前的fragment，
                    //然后显示homeFragment（不会重新初始化，只是加载之前隐藏的fragment）
                    fragmentTransaction1.hide(fragmentNow).show(homeFragment);
                }else {
                    //如果homeFragment不存在，则隐藏当前的fragment，
                    //然后添加homeFragment（此时是初始化）
                    fragmentTransaction1.hide(fragmentNow).add(R.id.another_fragment,homeFragment);
                }
                fragmentNow=homeFragment;
                fragmentTransaction1.commit();
                break;
            case 1://如果点击考友圈
                if(exchangeFragment.isAdded()){
                    fragmentTransaction1.hide(fragmentNow).show(exchangeFragment);
                }else{
                    fragmentTransaction1.hide(fragmentNow).add(R.id.another_fragment,exchangeFragment);
                }
                fragmentNow=exchangeFragment;
                fragmentTransaction1.commit();
                break;
            case 2://如果点击个人中心
                if(personalCenterFragment.isAdded()){
                    fragmentTransaction1.hide(fragmentNow).show(personalCenterFragment);
                }else {
                    fragmentTransaction1.hide(fragmentNow).add(R.id.another_fragment,personalCenterFragment);
                }
                fragmentNow=personalCenterFragment;
                fragmentTransaction1.commit();
                break;
            default:break;
        }

    }
}
