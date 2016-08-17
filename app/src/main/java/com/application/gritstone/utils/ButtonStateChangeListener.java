package com.application.gritstone.utils;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by guorenjie on 2016/5/26.
 */
public class ButtonStateChangeListener {
    private final static float[]BUTTON_PRESSED=new float[]{
        2.0f,0,0,0,-50,
        0,2.0f,0,0,-50,
        0,0,2.0f,0,-50,
        0,0,0,5,0};

/**
 * 按钮恢复原状
 */
    private final static float[]BUTTON_RELEASED=new float[]{
        1,0,0,0,0,
        0,1,0,0,0,
        0,0,1,0,0,
        0,0,0,1,0};

    public static final View.OnTouchListener touchListener=new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v,MotionEvent event){
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_PRESSED));
                v.setBackgroundDrawable(v.getBackground());
            }else if(event.getAction()==MotionEvent.ACTION_UP){
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_RELEASED));
                v.setBackgroundDrawable(v.getBackground());
            }
                return false;
            }
    };
}