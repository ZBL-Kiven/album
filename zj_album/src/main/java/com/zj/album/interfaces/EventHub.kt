package com.zj.album.interfaces

import com.zj.album.nModule.FileInfo

interface EventHub {

    fun onDataGot(data: List<FileInfo>?, dataAccessKey: String)

    fun onSelectedChanged(count: Int, selectedAccessKey: String)
}
