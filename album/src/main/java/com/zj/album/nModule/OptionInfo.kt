package com.zj.album.nModule


import com.zj.album.options.AlbumOptions
import com.zj.album.nutils.MimeType
import com.zj.album.options.MutableSizeOption
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import java.io.Serializable
import java.util.*

internal data class OptionInfo(
    internal val appName: String = "",
    internal var maxSelectSize: Int = Int.MAX_VALUE,
    internal var imgMinSize: Long = 0,
    internal var imgMaxSize: Long = Long.MAX_VALUE,
    internal var videoMinSize: Long = 0,
    internal var videoMaxSize: Long = Long.MAX_VALUE,
    internal var simultaneousSelection: Boolean = false,
    internal var sortWithDesc: Boolean = true,
    internal var useOriginDefault: Boolean = false,
    internal var originalPolymorphism: Boolean = false,
    internal var selectedUris: ArrayList<SimpleSelectInfo>? = null,
    internal var ignorePaths: ArrayList<String>? = arrayListOf(),
    internal var mimeType: EnumSet<MimeType>? = AlbumOptions.pairOf(AlbumOptions.ofImage(), AlbumOptions.ofVideo()),
    internal var pagerEffect: TransitionEffect = TransitionEffect.Zoom,
    internal var imageScaleEffect: ScaleEffect = ScaleEffect.CUBIC,
    internal var mutableSizeInfo: ArrayList<MutableSizeOption.MutableInfo>? = null): Serializable