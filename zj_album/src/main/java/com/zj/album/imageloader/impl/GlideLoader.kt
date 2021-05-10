package com.zj.album.imageloader.impl

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zj.album.imageloader.ImageLoader

internal class GlideLoader : ImageLoader {

    override fun loadThumbnail(imageView: ImageView, resize: Int, defaultDrawable: Int, path: Any?) {
        Glide.with(imageView).asBitmap().load(path).apply(RequestOptions().override(resize, resize).placeholder(defaultDrawable).centerCrop()).into(imageView)
    }

    override fun loadImage(imageView: ImageView, resizeW: Int, resizeH: Int, path: Any?) {
        Glide.with(imageView).load(path).apply(RequestOptions().override(resizeW, resizeH).priority(Priority.HIGH).fitCenter()).into(imageView)
    }
}
