package com.zj.album.ui.preview.images.transformer;


import androidx.viewpager.widget.ViewPager;
import android.view.View;
import org.jetbrains.annotations.NotNull;

/**
 * @author ZJJ on 2019.10.24
 */
abstract class PageTransformerEffect implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NotNull View view, float position) {
        if (position < -1.0f) {
            handleInvisiblePage(view, position);
        } else if (position <= 0.0f) {
            handleLeftPage(view, position);
        } else if (position <= 1.0f) {
            handleRightPage(view, position);
        } else {
            handleInvisiblePage(view, position);
        }
    }

    public abstract void handleInvisiblePage(View view, float position);

    public abstract void handleLeftPage(View view, float position);

    public abstract void handleRightPage(View view, float position);
}