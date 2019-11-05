package com.zj.album.nModule

import com.zj.album.nutils.AlbumOptions
import com.zj.album.nutils.MimeType
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

internal data class OptionInfo(internal val appName: String = "", internal var maxSelectSize: Int = Int.MAX_VALUE, internal var minSize: Long = 0, internal var simultaneousSelection: Boolean = false, internal var sortWithDesc: Boolean = true, internal var useOriginDefault: Boolean = false, internal var originalPolymorphism: Boolean = false, internal var selectedUris: ArrayList<SimpleSelectInfo>? = null, internal var ignorePaths: ArrayList<String>? = arrayListOf(), internal var mimeType: EnumSet<MimeType>? = AlbumOptions.pairOf(AlbumOptions.ofImage(), AlbumOptions.ofVideo())) : Serializable