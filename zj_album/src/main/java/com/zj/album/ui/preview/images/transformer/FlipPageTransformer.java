package com.zj.album.ui.preview.images.transformer;

import android.view.View;

/**
 * @author ZJJ on 2019.10.24
 */
@SuppressWarnings("unused")
public class FlipPageTransformer extends PageTransformer {
    private static final float ROTATION = 180.0f;

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setTranslationX( -view.getWidth() * position);
        float rotation = (ROTATION * position);
        view.setRotationY( rotation);

        if (position > -0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setTranslationX( -view.getWidth() * position);
        float rotation = (ROTATION * position);
        view.setRotationY( rotation);

        if (position < 0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

}