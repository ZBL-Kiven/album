package com.zj.album.nHelpers

import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.zj.album.*
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import com.zj.album.options.AlbumConfig
import com.zj.album.nutils.MimeType
import com.zj.album.nutils.TYPE_IMG
import com.zj.album.nutils.TYPE_VIDEO
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

internal class LocalDataExecute(private val enumSet: EnumSet<MimeType>?, useDesc: Boolean, imgMinSize: Long, imgMaxSize: Long, vMinSize: Long, vMaxSize: Long, private val ignorePaths: List<String>?, private val onDataGot: (ArrayList<FolderInfo>?) -> Unit) : Runnable {

    private var isDesc = false
    private var fileImgMinSize = 0L
    private var fileImgMaxSize = Long.MAX_VALUE
    private var fileVideoMinSize = 0L
    private var fileVideoMaxSize = Long.MAX_VALUE

    init {
        this.isDesc = useDesc
        this.fileImgMinSize = imgMinSize
        this.fileImgMaxSize = imgMaxSize
        this.fileVideoMinSize = vMinSize
        this.fileVideoMaxSize = vMaxSize
    }

    private val sortOrder: String; get() = MediaStore.Files.FileColumns.DATE_MODIFIED + if (isDesc) " DESC " else " ASC "
    private val searchUri = MediaStore.Files.getContentUri("external")

    @Suppress("DEPRECATION") private val projection: Array<String>
        get() {
            val lst = mutableListOf(MediaStore.Video.Media._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                lst.add(MediaStore.MediaColumns.DURATION)
            }
            return lst.toTypedArray()
        }

    override fun run() {
        onDataGot(running())
    }

    private fun running(): ArrayList<FolderInfo>? {
        var mCursor: Cursor? = null
        try {
            val resolver = AlbumConfig.getContentResolver()
            val selectionAndArgs = getSelectionAndArgs(enumSet)
            mCursor = resolver?.query(searchUri, projection, selectionAndArgs, null, sortOrder + getRange(0, 0))
            if (mCursor == null) {
                return null
            }
            return subGroupOfImage(mCursor)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mCursor?.close()
        }
        return null
    }

    private fun subGroupOfImage(cursor: Cursor): ArrayList<FolderInfo>? {
        return try {
            val allInfo = arrayListOf<FileInfo>()
            while (cursor.moveToNext()) {
                val media = FileInfo.valueOf(cursor)
                val file = File(media.path)
                if (!file.exists() || file.isDirectory) {
                    continue
                }
                allInfo.add(media)
            }
            getFoldersData(allInfo)
        } catch (e: Exception) {
            null
        }
    }

    private fun getFoldersData(mData: List<FileInfo>?): ArrayList<FolderInfo>? {
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
        allInfo.topImgContentUri = allInfo.files?.get(0)?.getContentUri()
        allInfo.parentName = AlbumConfig.getString(R.string.pg_str_all)
        list.add(allInfo)
        groupData.forEach { (s, lst) ->
            val fInfo = FolderInfo(false)
            fInfo.files = lst
            fInfo.imageCounts = fInfo.files?.size ?: 0
            fInfo.topImgUri = fInfo.files?.get(0)?.path ?: ""
            fInfo.parentName = s
            list.add(fInfo)
        }
        return list
    }

    @Suppress("SameParameterValue")
    private fun getRange(limit: Int, offset: Int): String {
        val builder = StringBuilder()
        if (limit != 0) {
            builder.append("limit ").append(limit).append(" ")
            if (offset != 0) {
                builder.append("offset ").append(offset)
            }
        }
        return builder.toString()
    }

    private fun getSelectionAndArgs(enumSet: EnumSet<MimeType>?): String {
        val selection = StringBuilder()
        selection.append("(")
        val enumSets = mutableSetOf<Int>()
        enumSet?.forEach {
            if (!enumSets.contains(it.type)) enumSets.add(it.type)
        }
        enumSets.forEachIndexed { index, it ->
            if (index > 0) selection.append("OR ")
            selection.append(" (")
            when (it) {
                TYPE_IMG -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE} ").append("AND ").append(MediaStore.Files.FileColumns.SIZE).append(" BETWEEN $fileImgMinSize AND $fileImgMaxSize ")
                }
                TYPE_VIDEO -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO} ").append("AND ").append(MediaStore.Files.FileColumns.SIZE).append(" BETWEEN $fileVideoMinSize AND $fileVideoMaxSize ")
                }
            }
            selection.append(") ")
        }
        selection.append(") ")
        if (!enumSet.isNullOrEmpty()) {
            selection.append("AND (")
            enumSet.forEachIndexed { index, mimeType ->
                selection.append(MediaStore.Files.FileColumns.MIME_TYPE).append("=\'${mimeType.mMimeTypeName}\' ")
                if (index < enumSet.size - 1) {
                    selection.append("OR ")
                }
            }
            selection.append(") ")
            ignorePaths?.let {
                if (it.isEmpty()) return@let
                selection.append("AND (")
                it.forEachIndexed { index, ignore ->
                    selection.append("_data").append(" NOT LIKE \'%$ignore%\' ")
                    if (index < it.lastIndex) {
                        selection.append("AND ")
                    }
                }
                selection.append(") ")
            }
        }
        return selection.toString()
    }
}
