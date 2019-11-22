package com.zj.album.options

import com.zj.album.nutils.MimeType
import java.io.Serializable
import java.util.*

@Suppress("unused")
class MutableSizeOption(private val albumOptions: AlbumOptions) {

    private var mutableSizeOptions: ArrayList<MutableInfo>? = null

    fun addNewRule(name: String, size: Int, types: EnumSet<MimeType>): MutableSizeOption {
        if (mutableSizeOptions == null) mutableSizeOptions = ArrayList()
        mutableSizeOptions?.add(MutableInfo(name, size, types))
        return this
    }

    fun set(): AlbumOptions {
        return albumOptions.setMaxSizeForType(mutableSizeOptions)
    }

    internal data class MutableInfo(val name: String, val size: Int, val types: EnumSet<MimeType>) : Serializable
}