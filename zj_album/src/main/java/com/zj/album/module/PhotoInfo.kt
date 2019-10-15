package com.zj.album.module

import com.zj.album.common.MimeType

data class PhotoInfo(
    val parentFolderPath: String?,
    val path: String,
    val mimeType: MimeType,
    val duration: Long,
    val size: Long,
    var isSelected: Boolean,
    var useOriginal: Boolean
)
