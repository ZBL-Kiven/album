@file:Suppress("unused")

package com.zj.album.nHelpers

import com.zj.album.PhotoAlbum
import com.zj.album.R
import com.zj.album.interfaces.EventHub
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import java.util.*

private var mData: MutableList<FolderInfo>? = null
    get() {
        if (field == null) field = mutableListOf()
        return field
    }

private var selectedPaths: MutableList<FileInfo>? = null
    get() {
        if (field == null) field = mutableListOf();return field
    }
private var curAccessKey: String = ""

private val dataListeners: MutableMap<String, EventHub>? = mutableMapOf()

private var curDisplayFolder: FolderInfo? = null


sealed class DataHelper {

    private fun getFormSelectedPaths(path: String): FileInfo? {
        return selectedPaths?.firstOrNull { it.path == path }
    }

    fun findIsInSelectedPaths(path: String): Boolean {
        return getFormSelectedPaths(path) != null
    }

    fun getIndexOfSelected(path: String): Int {
        return selectedPaths?.indexOfFirst { it.path == path } ?: -1
    }

    fun putSelectedPath(info: FileInfo?) {
        if (info != null) {
            val selectedIndex = getIndexOfSelected(info.path)
            if (selectedIndex >= 0) selectedPaths?.set(selectedIndex, info)
            else selectedPaths?.add(info)
            selectedPaths?.sortBy { it.lastModifyTs }
        }
    }

    private fun removeFormSelectedPaths(path: String) {
        selectedPaths?.removeAll {
            it.path == path
        }
    }

    fun getSelectedCount(): Int {
        return selectedPaths?.size ?: 0
    }

    fun setData(data: FolderInfo?) {
        data?.files?.forEach {
            val path = it.path
            if (findIsInSelectedPaths(path)) {
                it.setSelected(true, ignoreMaxCount = true)
                it.useOriginal = getFormSelectedPaths(path)?.useOriginal ?: false
                putSelectedPath(it)
            }
        }
        curDisplayFolder = data
        curAccessKey = UUID.randomUUID().toString()
        dataListeners?.forEach {
            it.value.onDataGot(data?.files, curAccessKey)
        }
    }

    fun onSelectedChanged(select: Boolean, info: FileInfo, ignoreMaxCount: Boolean): Boolean {
        val canSelect = getSelectedCount() < PhotoAlbum.maxSelectSize
        if (select) {
            if (canSelect) putSelectedPath(info)
            else {
                if (!ignoreMaxCount) {
                    val toastStr = PhotoAlbum.getString(R.string.pg_str_at_best, PhotoAlbum.maxSelectSize)
                    PhotoAlbum.toastLong(toastStr)
                }
                return false
            }
        } else {
            removeFormSelectedPaths(info.path)
        }
        dataListeners?.forEach {
            it.value.onSelectedChanged(getSelectedCount())
        }
        return true
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
            putSelectedPath(FileInfo("", it.first, 0, it.second))
        }
    }

    @JvmStatic
    fun setLocalData(data: MutableList<FolderInfo>?) {
        mData = data
        curDisplayFolder = data?.firstOrNull { it.isAll }
        setData(curDisplayFolder)
    }

    fun register(regKey: String, accessKey: String, eventHub: EventHub) {
        dataListeners?.put(regKey, eventHub)
        if (accessKey != curAccessKey) curDisplayFolder?.let {
            eventHub.onDataGot(it.files, curAccessKey)
            val selectedCount = getSelectedCount()
            if (selectedCount > 0) eventHub.onSelectedChanged(selectedCount)
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
    fun getCurSelectedData(): List<FileInfo>? {
        return selectedPaths
    }

    @JvmStatic
    fun getFolderData(): MutableList<FolderInfo>? {
        return mData
    }

    @JvmStatic
    fun isSelected(path: String): Boolean {
        return findIsInSelectedPaths(path)
    }

    @JvmStatic
    fun indexOfSelected(path: String): Int {
        return getIndexOfSelected(path)
    }

    @JvmStatic
    fun isCurDisplayFolder(id: String): Boolean {
        return id == curDisplayFolder?.id
    }
}
