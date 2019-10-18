package com.zj.album.nHelpers

import com.zj.album.PhotoAlbum
import com.zj.album.R
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import java.io.File

internal object DataHelper {

    fun init(selectedPaths: Collection<Pair<String, Boolean>>?) {
        this.selectedPaths?.clear()
        selectedPaths?.forEach {
            this.selectedPaths?.put(it.first, it.second)
        }
    }

    private var mData: MutableList<FileInfo>? = null
        get() {
            if (field == null) field = mutableListOf()
            return field
        }

    private var selectedPaths: MutableMap<String, Boolean>? = null
        get() {
            if (field == null) field = mutableMapOf();return field
        }

    fun setData(data: MutableList<FileInfo>) {
        data.forEach {
            if (selectedPaths?.containsKey(it.path) == true) {
                it.isSelected = true
                it.useOriginal = selectedPaths?.get(it.path) ?: false
            }
        }
        this.mData = data
    }

    fun onSelectedChanged(fileInfo: FileInfo) {
        if (fileInfo.isSelected) {
            selectedPaths?.put(fileInfo.path, true)
        } else {
            selectedPaths?.remove(fileInfo.path)
        }

        mData?.firstOrNull {
            it.path == fileInfo.path
        }?.apply {
            this.isSelected = fileInfo.isSelected
            this.useOriginal = fileInfo.useOriginal
        }
    }

    fun getFoldersData(): ArrayList<FolderInfo>? {
        val files = ArrayList(mData ?: return null)
        val groupData = files.groupByTo(mutableMapOf()) {
            val file = File(it.path)
            if (file.exists()) {
                file.parentFile?.name ?: "other"
            } else {
                "other"
            }
        }
        if (groupData.isNullOrEmpty()) return null
        val list = ArrayList<FolderInfo>()
        val allInfo = FolderInfo(true)
        allInfo.files = files
        allInfo.imageCounts = allInfo.files?.size ?: 0
        allInfo.topImgUri = allInfo.files?.get(0)?.path ?: ""
        allInfo.parentName = PhotoAlbum.getString(R.string.pg_str_all)
        list.add(allInfo)
        groupData.forEach { (s, lst) ->
            val fInfo = FolderInfo(false)
            fInfo.files = lst
            fInfo.imageCounts = fInfo.files?.size ?: 0
            fInfo.topImgUri = fInfo.files?.get(0)?.path ?: ""
            fInfo.parentName = s
            list.add(allInfo)
        }
        return list
    }
}