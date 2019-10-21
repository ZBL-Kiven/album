package com.zj.album.graphy.preview;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Creat by zhaojie on 2017.10.30
 * 缩放移动属性动画
 */
public class ZoomPageTransformer implements ViewPager.PageTransformer {
    private float mMinScale = 0.96f;
    private float mMinAlpha = 0.65f;

    public ZoomPageTransformer() {
    }

    public ZoomPageTransformer(float minAlpha, float minScale) {
        setMinAlpha(minAlpha);
        setMinScale(minScale);
    }

    public void handleInvisiblePage(View view, float position) {
        view.setAlpha(1);
    }

    public void handleLeftPage(View view, float position) {
        float scale = Math.max(mMinScale, 1 + position);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setAlpha(mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
    }

    public void handleRightPage(View view, float position) {
        float scale = Math.max(mMinScale, 1 - position);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setAlpha(mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
    }

    public void setMinAlpha(float minAlpha) {
        if (minAlpha >= 0.6f && minAlpha <= 1.0f) {
            mMinAlpha = minAlpha;
        }
    }

    public void setMinScale(float minScale) {
        if (minScale >= 0.6f && minScale <= 1.0f) {
            mMinScale = minScale;
        }
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (position < -1.0f) {
            // [-Infinity,-1)
            // This page is way off-screen to the left.
            handleInvisiblePage(view, position);
        } else if (position <= 0.0f) {
            // [-1,0]
            // Use the default slide transition when moving to the left page
            handleLeftPage(view, position);
        } else if (position <= 1.0f) {
            // (0,1]
            handleRightPage(view, position);
        } else {
            // (1,+Infinity]
            // This page is way off-screen to the right.
            handleInvisiblePage(view, position);
        }
    }
}