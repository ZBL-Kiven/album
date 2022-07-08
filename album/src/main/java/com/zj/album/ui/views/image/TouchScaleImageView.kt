package com.zj.album.ui.views.image

import android.content.Context
import android.util.AttributeSet
import com.zj.album.options.AlbumConfig
import com.zj.album.ui.views.image.easing.ScaleEffect

internal class TouchScaleImageView : ImageViewTouch {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun getEasingEffect(): ScaleEffect {
        return AlbumConfig.imageScaleEffect
    }

}
