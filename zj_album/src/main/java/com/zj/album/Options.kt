package com.zj.album

import java.util.*

/**
 * @author ZJJ on 2019.10.24
 * */
@Suppress("unused")
data class Options(internal var req: Int, internal val onStart: (Options) -> Unit) {
    internal var maxSelectSize: Int = Int.MAX_VALUE
    internal var minSize: Long = 0
    internal var simultaneousSelection: Boolean = false
    internal var sortWithDesc: Boolean = true
    internal var useOriginDefault: Boolean = false
    internal var originalPolymorphism: Boolean = false
    internal var selectedUris: Collection<Pair<String, Boolean>>? = null
    internal var ignorePaths: Array<String>? = arrayOf()
    internal var mimeType: EnumSet<MimeType>? = pairOf(ofImage(), ofVideo())

    fun maxSelectedCount(count: Int): Options {
        this.maxSelectSize = count
        return this
    }

    fun minFilterSize(minSize: Long): Options {
        this.minSize = minSize
        return this
    }

    fun simultaneousSelection(ssv: Boolean): Options {
        this.simultaneousSelection = ssv
        return this
    }

    fun sortWithDesc(useDesc: Boolean): Options {
        this.sortWithDesc = useDesc
        return this
    }

    fun useOriginDefault(isOriginal: Boolean): Options {
        this.useOriginDefault = isOriginal
        return this
    }

    fun setOriginalPolymorphism(polymorphism: Boolean): Options {
        this.originalPolymorphism = polymorphism
        return this
    }

    fun selectedUris(uris: Collection<Pair<String, Boolean>>): Options {
        this.selectedUris = uris
        return this
    }

    fun ignorePaths(vararg paths: String): Options {
        this.ignorePaths = arrayOf(*paths)
        return this
    }

    fun mimeTypes(types: EnumSet<MimeType>): Options {
        this.mimeType = types
        return this
    }

    fun start() {
        onStart(this)
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
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


