package com.zj.album.nModule

/*
* Created by ZJJ on 2019/10/16
* */
data class FolderInfo(
    val isAll: Boolean
) {
    var parentName: String = ""
    var imageCounts: Int = 0
    var topImgUri: String? = null
    var files: List<FileInfo>? = null
}
