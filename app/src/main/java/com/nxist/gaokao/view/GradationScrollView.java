package com.nxist.gaokao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * 自定义ScrollView，实现像QQ滑动标题栏颜色改变的效果
 * Created by xym760 on 2018/4/1.
 */

public class GradationScrollView extends ScrollView {

    public interface ScrollViewListener {

        void onScrollChanged(GradationScrollView scrollView, int x, int y,
                             int oldx, int oldy);

        void onScrollBottom(GradationScrollView scrollView);

    }

    private ScrollViewListener scrollViewListener = null;

    public GradationScrollView(Context context) {
        super(context);
    }

    public GradationScrollView(Context context, AttributeSet attrs,
                               int defStyle) {
        super(context, attrs, defStyle);
    }

    public GradationScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        //如果滚动到最底部
        if(clampedY){
            scrollViewListener.onScrollBottom(this);
        }
    }
}
