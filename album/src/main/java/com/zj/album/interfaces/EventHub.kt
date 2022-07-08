package com.zj.album.interfaces

import com.zj.album.nModule.FileInfo

/**
 * @author ZJJ on 2019.10.24
 * */
internal interface EventHub {

    fun onDataGot(data: List<FileInfo>?, dataAccessKey: String)

    fun onSelectedChanged(count: Int, selectedAccessKey: String)
}
