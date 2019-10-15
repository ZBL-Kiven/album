package com.zj.album.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by zhaojie on 2017/10/10.
 */

public class ImageLoaderUtils {

    public static void load(ImageView view, String url) {
        Glide.with(view)
                .load(url)
                .into(view);
    }
}
