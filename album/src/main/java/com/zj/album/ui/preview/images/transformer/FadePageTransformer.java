package com.zj.album.ui.preview.images.transformer;

import android.view.View;

/**
 * @author ZJJ on 2019.10.24
 */
@SuppressWarnings("unused")
class FadePageTransformer extends PageTransformerEffect {

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