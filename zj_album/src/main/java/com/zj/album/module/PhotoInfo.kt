package com.zj.album.module

import com.zj.album.common.MimeType

data class PhotoInfo(
    val parentFolderPath: String?,
    val path: String,
    val mimeType: MimeType,
    var useOriginal: Boolean,
    var duration: Long,
    var size: Long,
    var isSelected: Boolean
)
