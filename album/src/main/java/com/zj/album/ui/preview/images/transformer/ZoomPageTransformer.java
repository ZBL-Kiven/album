package com.zj.album.ui.preview.images.transformer;

import android.view.View;


/**
 * @author ZJJ on 2019.10.24
 */
@SuppressWarnings({"WeakerAccess", "unused"})
class ZoomPageTransformer extends PageTransformerEffect {
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
        view.setAlpha( 0);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        float scale = Math.max(mMinScale, 1 + position);
        float verMargin = view.getHeight() * (1 - scale) / 2;
        float horMargin = view.getWidth() * (1 - scale) / 2;
        view.setTranslationX( horMargin - verMargin / 2);
        view.setScaleX( scale);
        view.setScaleY( scale);
        view.setAlpha( mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
    }

    @Override
    public void handleRightPage(View view, float position) {
        float scale = Math.max(mMinScale, 1 - position);
        float vertMargin = view.getHeight() * (1 - scale) / 2;
        float horzMargin = view.getWidth() * (1 - scale) / 2;
        view.setTranslationX( -horzMargin + vertMargin / 2);
        view.setScaleX( scale);
        view.setScaleY( scale);
        view.setAlpha( mMinAlpha + (scale - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
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