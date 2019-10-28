package com.zj.album.ui.preview.images.transformer;

import android.view.View;

/**
 * Created by ZJJ on 2019.10.24
 */
@SuppressWarnings({"unused"})
public class AccordionPageTransformer extends PageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setPivotX(view.getWidth());
        view.setScaleX(1.0f + position);
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setPivotX(0);
        view.setScaleX(1.0f - position);
        view.setAlpha(1);
    }

}