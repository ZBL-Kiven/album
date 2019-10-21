package com.zj.album.imageloader;

import android.net.Uri;
import android.widget.ImageView;

/**
 * 图片加载引擎
 *
 * @author yangji
 */
public interface ImageLoader {

    /**
     * 加载缩略图
     *
     * @param imageView       需要加载的图片
     * @param resize          图片大小
     * @param defaultDrawable 默认展示图片
     * @param path             图片URL
     */
    void loadThumbnail(ImageView imageView, int resize, int defaultDrawable, String path);


    /**
     * 加载全图
     *
     * @param imageView 控件
     * @param resizeW   宽度
     * @param resizeH   高度
     * @param path       图片地址
     */
    void loadImage(ImageView imageView, int resizeW, int resizeH, String path);
}
