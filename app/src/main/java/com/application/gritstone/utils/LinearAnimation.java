package com.application.gritstone.utils;


import android.view.animation.Animation;
import android.view.animation.Transformation;


/**
 * Created by administrator-ypc on 2016/5/30.
 */
public class LinearAnimation extends Animation
{
    private LinearAnimationListener mListener = null;

    public interface LinearAnimationListener
    {
        void applyTans(float interpolatedTime);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        super.applyTransformation(interpolatedTime, t);
        if (mListener != null)
            mListener.applyTans(interpolatedTime);
    }

    public void setLinearAnimationListener(LinearAnimationListener listener)
    {
        mListener = listener;
    }
}
