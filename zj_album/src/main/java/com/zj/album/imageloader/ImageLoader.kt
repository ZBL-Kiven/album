package com.zj.album.imageloader

import android.widget.ImageView

internal interface ImageLoader {

    /**
     * loading thumbnail
     *
     * @param imageView       the image view
     * @param resize          resize to a square bitmap
     * @param defaultDrawable the default if loading or fail
     * @param path            resource url
     */
    fun loadThumbnail(imageView: ImageView, resize: Int, defaultDrawable: Int, path: Any?)


    /**
     * loading a full image
     *
     * @param imageView the image view
     * @param resizeW   resize bitmap width
     * @param resizeH   resize bitmap height
     * @param path      resource url
     */
    fun loadImage(imageView: ImageView, resizeW: Int, resizeH: Int, path: Any?)
}
