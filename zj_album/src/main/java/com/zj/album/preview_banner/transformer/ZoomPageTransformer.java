package com.zj.album.preview_banner.transformer;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Creat by zhaojie on 2017.10.30
 * 缩放移动属性动画
 */
public class ZoomPageTransformer extends PageTransformer {
    private float mMinScale = 0.96f;
    private float mMinAlpha = 0.65f;

    public ZoomPageTransformer() {
    }

    public ZoomPageTransformer(float minAlpha, float minScale) {
        setMinAlpha(minAlpha);
        setMinScale(minScale);
    }

    @Override
    public void handleInvisiblePage(View view, float position) {
        ViewHelper.setAlpha(view, 0);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        float scale = Math.max(mMinScale, 1 + position);
        float vertMargin = view.getHeight() * (1 - scale) / 2;
        float horzMargin = view.getWidth() * (1 - scale) / 2;
        ViewHelper.setTranslationX(view, horzMargin - vertMargin / 2);
        ViewHelper.setScaleX(view, scale);
        ViewHelper.setScaleY(view, scale);
        ViewHelper.setAlpha(view, mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
    }

    @Override
    public void handleRightPage(View view, float position) {
        float scale = Math.max(mMinScale, 1 - position);
        float vertMargin = view.getHeight() * (1 - scale) / 2;
        float horzMargin = view.getWidth() * (1 - scale) / 2;
        ViewHelper.setTranslationX(view, -horzMargin + vertMargin / 2);
        ViewHelper.setScaleX(view, scale);
        ViewHelper.setScaleY(view, scale);
        ViewHelper.setAlpha(view, mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
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
}