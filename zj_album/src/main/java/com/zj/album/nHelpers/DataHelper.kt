package com.zj.album.nHelpers

import com.zj.album.interfaces.EventHub
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo

private var mData: MutableList<FolderInfo>? = null
    get() {
        if (field == null) field = mutableListOf()
        return field
    }

private var selectedPaths: MutableMap<String, FileInfo>? = null
    get() {
        if (field == null) field = mutableMapOf();return field
    }

private val dataListeners: MutableMap<String, EventHub>? = mutableMapOf()

private var curDisplayFolder: FolderInfo? = null


sealed class DataHelper {

    fun setData(data: FolderInfo?) {
        data?.files?.forEach {
            val path = it.path
            if (selectedPaths?.containsKey(path) == true) {
                it.isSelected = true
                it.useOriginal = selectedPaths?.get(path)?.useOriginal ?: false
                selectedPaths?.put(path, it)
            }
        }
        curDisplayFolder = data
    }

    fun onSelectedChanged(fileInfo: FileInfo) {
        if (fileInfo.isSelected) {
            selectedPaths?.put(fileInfo.path, fileInfo)
        } else {
            selectedPaths?.remove(fileInfo.path)
        }
        curDisplayFolder?.files?.firstOrNull {
            it.path == fileInfo.path
        }?.apply {
            if (this.isSelected != fileInfo.isSelected) {
                this.isSelected = fileInfo.isSelected
                this.useOriginal = fileInfo.useOriginal
            }
        }
    }

    fun clear() {
        curDisplayFolder = null
        selectedPaths?.clear()
        mData?.clear()
    }
}

object DataProxy : DataHelper() {
    @JvmStatic
    fun init(selected: Collection<Pair<String, Boolean>>?) {
        selectedPaths?.clear()
        selected?.forEach {
            selectedPaths?.put(it.first, FileInfo("", it.first, "", 0, it.second))
        }
    }

    @JvmStatic
    fun setLocalData(data: MutableList<FolderInfo>) {
        mData = data
        curDisplayFolder = data.firstOrNull { it.isAll }
        setData(curDisplayFolder)
    }

    fun register(regKey: String, eventHub: EventHub) {
        dataListeners?.put(regKey, eventHub)
        curDisplayFolder?.let {
            eventHub.onDataGot(it.files)
        }
    }

    fun unregister(regKey: String) {
        dataListeners?.remove(regKey)
    }
}

object DataStore : DataHelper() {
    @JvmStatic
    fun getCurData(): FolderInfo? {
        return curDisplayFolder
    }

    @JvmStatic
    fun getCurSelectedData(): ArrayList<FileInfo>? {
        return curDisplayFolder?.files?.filterTo(arrayListOf()) {
            isSelected(it.path)
        }
    }

    @JvmStatic
    fun getFolderData(): MutableList<FolderInfo>? {
        return mData
    }

    @JvmStatic
    fun isSelected(path: String): Boolean {
        return selectedPaths?.containsKey(path) ?: false
    }

    @JvmStatic
    fun isCurDisplayFolder(id: String): Boolean {
        return id == curDisplayFolder?.id
    }
}
