package com.zj.album.ui.preview.images.transformer;

import android.view.View;

/**
 * Created by ZJJ on 2019.10.24
 */
@SuppressWarnings("unused")
public class FadePageTransformer extends PageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setTranslationX( -view.getWidth() * position);
        view.setAlpha( 1 + position);
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setTranslationX( -view.getWidth() * position);
        view.setAlpha( 1 - position);
    }

}