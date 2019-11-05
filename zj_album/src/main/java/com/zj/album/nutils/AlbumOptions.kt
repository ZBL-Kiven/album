package com.zj.album.nutils

import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.OptionInfo
import com.zj.album.nModule.SimpleSelectInfo
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

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

    fun minFilterSize(minSize: Long): AlbumOptions {
        options.minSize = minSize
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
                requestSet?.addAll(it) ?: { requestSet = EnumSet.copyOf(it) }.invoke()
            }
            return requestSet
        }
    }
}


