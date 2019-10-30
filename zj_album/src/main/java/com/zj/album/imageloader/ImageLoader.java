package com.zj.album.imageloader;

import android.widget.ImageView;

public interface ImageLoader {

    /**
     * loading thumbnail
     *
     * @param imageView       the image view
     * @param resize          resize to a square bitmap
     * @param defaultDrawable the default if loading or fail
     * @param path            resource url
     */
    void loadThumbnail(ImageView imageView, int resize, int defaultDrawable, String path);


    /**
     * loading a full image
     *
     * @param imageView the image view
     * @param resizeW   resize bitmap width
     * @param resizeH   resize bitmap height
     * @param path      resource url
     */
    void loadImage(ImageView imageView, int resizeW, int resizeH, String path);
}
