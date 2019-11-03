@file:Suppress("unused")

package com.zj.album.nHelpers

import com.zj.album.PhotoAlbum
import com.zj.album.R
import com.zj.album.interfaces.EventHub
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import com.zj.album.nutils.Constance
import java.util.*

private var useOriginal = false
private var curDataAccessKey: String = ""
private var curSelectedAccessKey: String = ""

private var mData: MutableList<FolderInfo>? = null
    get() {
        if (field == null) field = mutableListOf()
        return field
    }

private var selectedPaths: MutableList<FileInfo>? = null
    get() {
        if (field == null) field = mutableListOf();return field
    }

private val dataListeners: MutableMap<String, EventHub>? = mutableMapOf()

private var curDisplayFolder: FolderInfo? = null

sealed class DataHelper {

    protected fun getFormSelectedPaths(path: String): FileInfo? {
        return selectedPaths?.firstOrNull { it.path == path }
    }

    protected fun findIsInSelectedPaths(path: String): Boolean {
        return getFormSelectedPaths(path) != null
    }

    protected fun getIndexOfSelected(path: String): Int {
        return selectedPaths?.indexOfFirst { it.path == path } ?: -1
    }

    protected fun putSelectedPath(info: FileInfo?) {
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

    protected fun getSelectedCount(): Int {
        return selectedPaths?.size ?: 0
    }


    protected fun putASelect(select: Boolean, info: FileInfo) {
        if (select) {
            putSelectedPath(info)
        } else {
            info.useOriginalImages = false
            removeFormSelectedPaths(info.path)
        }
        curSelectedAccessKey = UUID.randomUUID().toString()
        dataListeners?.forEach {
            it.value.onSelectedChanged(getSelectedCount(), curSelectedAccessKey)
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
            putSelectedPath(FileInfo(it.first, "", 0, it.second))
        }
    }

    @JvmStatic
    fun setLocalData(data: MutableList<FolderInfo>?) {
        mData = data
        curDisplayFolder = data?.firstOrNull { it.isAll }
        setData(curDisplayFolder)
    }

    fun setData(data: FolderInfo?) {
        data?.files?.forEach {
            val path = it.path
            getFormSelectedPaths(path)?.let { f ->
                it.setSelected(f.isSelected(), ignoreMaxCount = true)
                it.useOriginalImages = f.useOriginalImages
                putSelectedPath(it)
            }
        }
        curDisplayFolder = data
        curSelectedAccessKey = UUID.randomUUID().toString()
        dataListeners?.forEach {
            it.value.onSelectedChanged(getSelectedCount(), curSelectedAccessKey)
        }
        curDataAccessKey = UUID.randomUUID().toString()
        dataListeners?.forEach {
            it.value.onDataGot(data?.files, if (data != null) curDataAccessKey else "")
        }
    }

    fun onSelectedChanged(select: Boolean, info: FileInfo, ignoreMaxCount: Boolean): Boolean {
        val selectedCount = getSelectedCount()
        val isNotMaxSized = selectedCount < PhotoAlbum.maxSelectSize
        val isVideoSelected = info.isVideo
        fun toast(id: Int, vararg args: Any) {
            if (!ignoreMaxCount) PhotoAlbum.toastLong(id, *args)
        }

        val canSelect = when {
            !select -> true
            PhotoAlbum.simultaneousSelection -> isNotMaxSized // not supported simultaneous selection
            selectedCount <= 0 -> true
            isNotMaxSized -> {
                val selectedHasVideo = (selectedPaths?.firstOrNull { it.isVideo } != null)
                when {
                    selectedHasVideo && isVideoSelected -> {
                        toast(R.string.pg_str_video_choose_tip_message)
                        false
                    }
                    selectedHasVideo || isVideoSelected -> {
                        toast(R.string.pg_str_images_choose_tip_message)
                        false
                    }
                    else -> {
                        true
                    }
                }
            }
            else -> {
                toast(R.string.pg_str_at_best, PhotoAlbum.maxSelectSize)
                false
            }
        }
        if (canSelect) putASelect(select, info)
        return canSelect
    }

    fun onOriginalChanged(original: Boolean, path: String): Boolean {
        if (!PhotoAlbum.useOriginalPolymorphism) {
            setUseOriginal(original)
            selectedPaths?.forEach {
                it.useOriginalImages = original
            }
        } else {
            if (!findIsInSelectedPaths(path) && original) {
                val fInfo = curDisplayFolder?.files?.firstOrNull { it.path == path }
                fInfo?.useOriginalImages = original
                if (fInfo != null) if (!onSelectedChanged(true, fInfo, false)) return false
            } else {
                getFormSelectedPaths(path)?.useOriginalImages = original
            }
        }
        return true
    }

    fun register(regKey: String, accessKey: String, selectedAccessKey: String, eventHub: EventHub) {
        dataListeners?.put(regKey, eventHub)
        curDisplayFolder?.let {
            if (accessKey != curDataAccessKey) eventHub.onDataGot(it.files, curDataAccessKey)
            if (selectedAccessKey != curSelectedAccessKey) {
                eventHub.onSelectedChanged(getSelectedCount(), curSelectedAccessKey)
            }
        } ?: eventHub.onDataGot(null, accessKey)
    }

    fun unregister(regKey: String) {
        dataListeners?.remove(regKey)
    }

    fun setUseOriginal(useOrigin: Boolean) {
        useOriginal = useOrigin
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

    fun isOriginalData(path: String): Boolean {
        if (path.isEmpty()) return false
        if (!PhotoAlbum.useOriginalPolymorphism) return useOriginal
        return getFormSelectedPaths(path)?.useOriginalImages ?: false
    }

    @JvmStatic
    fun curSelectedCount(): Int {
        return getSelectedCount()
    }

    @JvmStatic
    fun isOriginalInAutoMode(): Int {
        return when {
            PhotoAlbum.useOriginalPolymorphism -> return Constance.ORIGINAL_POLY
            useOriginal -> return Constance.ORIGINAL_AUTO_SET
            !useOriginal -> return Constance.ORIGINAL_AUTO_NOT
            else -> Constance.CODE_UNKNOWN
        }
    }

    @JvmStatic
    fun hasImageSelected(): Boolean {
        return (selectedPaths?.any { !it.isVideo } ?: false)
    }
}
