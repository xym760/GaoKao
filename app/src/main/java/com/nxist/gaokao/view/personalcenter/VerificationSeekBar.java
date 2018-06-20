package com.nxist.gaokao.view.personalcenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerificationSeekBar extends android.support.v7.widget.AppCompatSeekBar {
    //index为滑块大致宽度
    private int index = 150;
    private boolean k = true;

    public VerificationSeekBar(Context context) {
        super(context);
    }

    public VerificationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerificationSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            k = true;
            if (x - index > 20) {//如果点击位置未在滑块附件，则不分发事件
                k = false;
                return false;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            if (!k){
                return false;
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
