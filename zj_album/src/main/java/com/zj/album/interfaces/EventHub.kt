package com.zj.album.interfaces

import com.zj.album.nModule.FileInfo

interface EventHub {

    fun onSelectedChanged()

    fun onDataGot(data: List<FileInfo>?)

    fun onOriginalCheckedChanged(useOriginal: Boolean)

}
