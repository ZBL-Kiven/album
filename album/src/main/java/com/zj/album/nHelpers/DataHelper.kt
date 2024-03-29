@file:Suppress("unused")

package com.zj.album.nHelpers

import com.zj.album.options.AlbumConfig
import com.zj.album.R
import com.zj.album.interfaces.EventHub
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import com.zj.album.nModule.SimpleSelectInfo
import com.zj.album.nutils.Constance
import java.util.*
import kotlin.collections.ArrayList

private var useOriginal = false
private var curDataAccessKey: String = ""
private var curSelectedAccessKey: String = "init"

private var mData: MutableList<FolderInfo>? = null
    get() {
        if (field == null) field = mutableListOf()
        return field
    }

private var maxMutableSelectSize: MutableMap<String, Int>? = null
    get() {
        if (field == null) field = mutableMapOf()
        return field
    }

private var selectedPaths: ArrayList<FileInfo>? = null
    get() {
        if (field == null) field = arrayListOf();return field
    }

private val dataListeners: MutableMap<String, EventHub> = mutableMapOf()

private var curDisplayFolder: FolderInfo? = null

internal sealed class DataHelper {

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
            if (selectedIndex >= 0) {
                info.useOriginalImages = selectedPaths?.get(selectedIndex)?.useOriginalImages ?: false
                selectedPaths?.set(selectedIndex, info)
            } else {
                if (!AlbumConfig.useOriginalPolymorphism) {
                    info.useOriginalImages = useOriginal
                }
            }
            selectedPaths?.add(info)
            selectedPaths?.sortBy { it.lastModifyTs }
        }
    }

    protected fun mutableSizeFilter(info: FileInfo): Triple<String, Int, Boolean> {
        var typeName = ""
        return AlbumConfig.maxSelectSizeWithType?.let {
            it.firstOrNull { d ->
                d.types.firstOrNull { mt ->
                    mt.mMimeTypeName.equals(info.mimeType, true)
                } != null
            }?.let { mi ->
                val selectedSize = selectedPaths?.filter { fInfo ->
                    mi.types.mapTo(arrayListOf()) { t -> t.mMimeTypeName }.contains(fInfo.mimeType)
                }?.size ?: 0
                val canSelect = selectedSize < mi.size
                if (!canSelect) typeName = mi.name
                Triple(typeName, mi.size, canSelect)
            } ?: Triple("", 0, false)
        } ?: Triple("", 0, false)
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
        dataListeners.forEach {
            it.value.onSelectedChanged(getSelectedCount(), curSelectedAccessKey)
        }
    }

    fun clear() {
        curDisplayFolder = null
        selectedPaths?.clear()
        mData?.clear()
    }
}

internal object DataProxy : DataHelper() {
    @JvmStatic
    fun init(selected: Collection<SimpleSelectInfo>?) {
        selectedPaths?.clear()
        selected?.forEach {
            putSelectedPath(FileInfo(it.path, "", 0, -1, it.useOriginal))
        }
    }

    @JvmStatic
    internal fun setLocalData(data: MutableList<FolderInfo>?) {
        mData = data
        curDisplayFolder = data?.firstOrNull { it.isAll }
        setData(curDisplayFolder)
    }

    internal fun setData(data: FolderInfo?) {
        data?.files?.forEach {
            val path = it.path
            getFormSelectedPaths(path)?.let { f ->
                it.setSelected(f.isSelected(), ignoreMaxCount = true)
                it.useOriginalImages = f.useOriginalImages
            }
        }
        curDisplayFolder = data
        curDataAccessKey = UUID.randomUUID().toString()
        dataListeners.forEach {
            it.value.onDataGot(data?.files, if (data != null) curDataAccessKey else "")
        }
        val selectedCount = getSelectedCount()
        dataListeners.forEach {
            it.value.onSelectedChanged(selectedCount, curSelectedAccessKey)
        }
    }

    fun onSelectedChanged(select: Boolean, info: FileInfo, ignoreMaxCount: Boolean): Boolean {
        val selectedCount = getSelectedCount()
        val mutableSize = AlbumConfig.maxSelectSizeWithType
        val isMutableSize = !mutableSize.isNullOrEmpty()
        var typeName = ""
        var maxCount = AlbumConfig.maxSelectSize
        val isNotMaxSized = if (isMutableSize) {
            val filterSet = mutableSizeFilter(info)
            typeName = filterSet.first
            maxCount = filterSet.second
            filterSet.third
        } else {
            selectedCount < AlbumConfig.maxSelectSize
        }
        val isVideoSelected = info.isVideo
        fun toast(id: Int, vararg args: Any) {
            if (!ignoreMaxCount) AlbumConfig.toastLong(id, *args)
        }

        val canSelect = when {
            !select -> true
            AlbumConfig.simultaneousSelection -> {
                if (!isNotMaxSized && isMutableSize) {
                    toast(R.string.pg_str_at_best, typeName, maxCount)
                }
                isNotMaxSized
            } // not supported simultaneous selection
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
                toast(R.string.pg_str_at_best, typeName, maxCount)
                false
            }
        }
        if (canSelect) putASelect(select, info)
        return canSelect
    }

    fun onOriginalChanged(original: Boolean, path: String): Boolean {
        if (!AlbumConfig.useOriginalPolymorphism) {
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
        dataListeners[regKey] = eventHub
        curDisplayFolder?.let {
            val isDataAccess = accessKey != curDataAccessKey
            val isSelectedAccess = selectedAccessKey != curSelectedAccessKey
            val selectedCount = getSelectedCount()
            if (isDataAccess) eventHub.onDataGot(it.files, curDataAccessKey)
            if (isSelectedAccess) {
                eventHub.onSelectedChanged(selectedCount, curSelectedAccessKey)
            }
        } ?: eventHub.onDataGot(null, accessKey)
    }

    fun unregister(regKey: String) {
        dataListeners.remove(regKey)
    }

    fun setUseOriginal(useOrigin: Boolean) {
        useOriginal = useOrigin
    }
}

internal object DataStore : DataHelper() {
    @JvmStatic
    fun getCurData(): FolderInfo? {
        return curDisplayFolder
    }

    @JvmStatic
    fun getCurSelectedData(): ArrayList<FileInfo> {
        return selectedPaths ?: arrayListOf()
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
        if (!AlbumConfig.useOriginalPolymorphism) return useOriginal
        return getFormSelectedPaths(path)?.useOriginalImages ?: false
    }

    @JvmStatic
    fun curSelectedCount(): Int {
        return getSelectedCount()
    }

    @JvmStatic
    fun isOriginalInAutoMode(): Int {
        return when {
            AlbumConfig.useOriginalPolymorphism -> return Constance.ORIGINAL_POLY
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
