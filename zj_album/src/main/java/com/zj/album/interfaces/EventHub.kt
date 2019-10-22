package com.zj.album.interfaces

import com.zj.album.nModule.FileInfo

interface EventHub {

    fun onDataGot(data: List<FileInfo>?, curAccessKey: String)

    fun onOriginalCheckedChanged(useOriginal: Boolean, curAccessKey: String)

    fun onSelectedChanged(count: Int)
}
