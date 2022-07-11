package com.zj.album.options

import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.OptionInfo
import com.zj.album.nModule.SimpleSelectInfo
import com.zj.album.nutils.MimeType
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import java.io.Serializable
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author ZJJ on 2019.10.24
 * */
@Suppress("unused")
class AlbumOptions internal constructor(internal val onStart: (OptionInfo, call: (isCancel: Boolean, data: List<FileInfo>?) -> Unit) -> Unit) : Serializable {

    private var options: OptionInfo = OptionInfo()

    fun maxSelectedCount(count: Int): AlbumOptions {
        options.maxSelectSize = count
        return this
    }

    fun imgSizeRange(start: Long, end: Long): AlbumOptions {
        var trueEnd = end
        if (start < 0 || end < 0) {
            throw IllegalArgumentException("the image size range must not contain to negative numbers!")
        }
        if (start == end) trueEnd++
        options.imgMinSize = min(start, trueEnd)
        options.imgMaxSize = max(start, trueEnd)
        return this
    }

    fun videoSizeRange(start: Long, end: Long): AlbumOptions {
        var trueEnd = end
        if (start < 0 || end < 0) {
            throw IllegalArgumentException("the video size range must not contain to negative numbers!")
        }
        if (start == end) trueEnd++
        options.videoMinSize = min(start, trueEnd)
        options.videoMaxSize = max(start, trueEnd)
        return this
    }

    fun simultaneousSelection(ssv: Boolean): AlbumOptions {
        options.simultaneousSelection = ssv
        return this
    }

    fun sortWithDesc(useDesc: Boolean): AlbumOptions {
        options.sortWithDesc = useDesc
        return this
    }

    fun useOriginDefault(isOriginal: Boolean): AlbumOptions {
        options.useOriginDefault = isOriginal
        return this
    }

    fun setOriginalPolymorphism(polymorphism: Boolean): AlbumOptions {
        options.originalPolymorphism = polymorphism
        return this
    }

    fun selectedUris(uris: ArrayList<SimpleSelectInfo>): AlbumOptions {
        options.selectedUris = uris
        return this
    }

    fun ignorePaths(vararg paths: String): AlbumOptions {
        options.ignorePaths = arrayListOf(*paths)
        return this
    }

    fun mimeTypes(types: EnumSet<MimeType>?): AlbumOptions {
        options.mimeType = types
        return this
    }

    fun pagerTransitionEffect(effect: TransitionEffect): AlbumOptions {
        options.pagerEffect = effect
        return this
    }

    fun imageScaleEffect(effect: ScaleEffect): AlbumOptions {
        options.imageScaleEffect = effect
        return this
    }

    /**
     * set the size with a mime type
     * */
    fun mutableTypeSize(): MutableSizeOption {
        return MutableSizeOption(this)
    }

    internal fun setMaxSizeForType(op: ArrayList<MutableSizeOption.MutableInfo>?): AlbumOptions {
        op?.sortBy { it.size }
        this.options.mutableSizeInfo = op
        return this
    }

    fun start(call: (isCancel: Boolean, data: List<FileInfo>?) -> Unit) {
        onStart(options, call)
    }

    companion object {
        @JvmStatic
        fun ofImage(): EnumSet<MimeType> {
            return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF)
        }

        @JvmStatic
        fun ofStaticImage(): EnumSet<MimeType> {
            return EnumSet.of(MimeType.JPEG, MimeType.PNG)
        }

        @JvmStatic
        fun ofVideo(): EnumSet<MimeType> {
            return EnumSet.of(MimeType.MPEG, MimeType.MP4, MimeType.AVI, MimeType.MKV, MimeType.QUICKTIME, MimeType.THREEGPP, MimeType.THREEGPP2, MimeType.TS, MimeType.WEBM)
        }

        @JvmStatic
        fun ofAll(): EnumSet<MimeType> {
            return EnumSet.allOf(MimeType::class.java)
        }

        @JvmStatic
        fun pairOf(vararg types: MimeType): EnumSet<MimeType> {
            return EnumSet.copyOf(setOf(*types))
        }

        @JvmStatic
        fun pairOf(vararg types: EnumSet<MimeType>): EnumSet<MimeType>? {
            if (types.isEmpty()) return null
            var requestSet: EnumSet<MimeType>? = null
            types.forEach {
                requestSet?.addAll(it) ?: run {
                    requestSet = EnumSet.copyOf(it)
                }
            }
            return requestSet
        }
    }
}


