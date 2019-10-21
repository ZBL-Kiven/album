package com.zj.album.imageloader.impl;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.zj.album.imageloader.ImageLoader;

/**
 * @author yangji
 */
public class GlideLoader implements ImageLoader {

    @Override
    public void loadThumbnail(ImageView imageView, int resize, int defaultDrawable, String path) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .placeholder(defaultDrawable)
                        .centerCrop())
                .into(imageView);
    }

    @Override
    public void loadImage(ImageView imageView, int resizeW, int resizeH, String path) {
        Glide.with(imageView)
                .load(path)
                .apply(new RequestOptions()
                        .override(resizeW, resizeH)
                        .priority(Priority.HIGH)
                        .fitCenter())
                .into(imageView);
    }
}
