package com.zj.album.ui.preview.images.transformer;

import android.view.View;


/**
 * @author ZJJ on 2019.10.24
 */
class StackPageTransformer extends PageTransformerEffect {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setTranslationX(-view.getWidth() * position);
    }

}