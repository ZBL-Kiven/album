package com.zj.album.ui.preview.images.transformer;

import android.view.View;


/**
 * @author ZJJ on 2019.10.24
 */
public class ZoomCenterPageTransformer extends PageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setTranslationX( -view.getWidth() * position);

        view.setPivotX( view.getWidth() * 0.5f);
        view.setPivotY( view.getHeight() * 0.5f);
        view.setScaleX( 1 + position);
        view.setScaleY( 1 + position);

        if (position < -0.95f) {
            view.setAlpha( 0);
        } else {
            view.setAlpha( 1);
        }
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setTranslationX( -view.getWidth() * position);

        view.setPivotX( view.getWidth() * 0.5f);
        view.setPivotY( view.getHeight() * 0.5f);
        view.setScaleX( 1 - position);
        view.setScaleY( 1 - position);

        if (position > 0.95f) {
            view.setAlpha( 0);
        } else {
            view.setAlpha( 1);
        }
    }

}