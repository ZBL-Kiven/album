package com.zj.album.nModule

import java.util.*

/**
* @author ZJJ on 2019.10.24
* */
data class FolderInfo(
    val isAll: Boolean
) {
    var id: String = UUID.randomUUID().toString()
    var parentName: String = ""
    var imageCounts: Int = 0
    var topImgUri: String? = null
    var files: List<FileInfo>? = null
}
