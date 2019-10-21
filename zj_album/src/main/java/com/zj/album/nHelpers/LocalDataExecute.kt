package com.zj.album.nHelpers

import android.database.Cursor
import android.provider.MediaStore
import com.zj.album.*
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import com.zj.album.nutils.log
import java.io.File
import java.util.*
import java.util.concurrent.Callable
import kotlin.collections.ArrayList

internal class LocalDataExecute(
    private val enumSet: EnumSet<MimeType>?,
    useDesc: Boolean,
    minSize: Long,
    private val ignorePaths: Array<String>?
) : Callable<ArrayList<FolderInfo>?> {

    private var isDesc = false
    private var fileMinSize = 1L

    init {
        this.isDesc = useDesc
        this.fileMinSize = minSize
    }

    private val sortOrder: String; get() = MediaStore.Files.FileColumns.DATE_MODIFIED + if (isDesc) " DESC " else " ASC "
    private val searchUri = MediaStore.Files.getContentUri("external")
    private val projection = arrayOf(
        MediaStore.Files.FileColumns.RELATIVE_PATH,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Video.VideoColumns.DURATION
    )

    override fun call(): ArrayList<FolderInfo>? {
        var mCursor: Cursor? = null
        try {
            val resolver = PhotoAlbum.getContentResolver()
            val selectionAndArgs = getSelectionAndArgs(enumSet)
            mCursor = resolver?.query(
                searchUri, projection, selectionAndArgs.first,
                selectionAndArgs.second, sortOrder + getRange(0, 0)
            )
            if (mCursor == null) {
                return null
            }
            return subGroupOfImage(mCursor)
        } catch (e: Exception) {
            log("album loaded fail!")
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
                if (!file.exists() || file.isDirectory || !file.canRead() || file.length() <= fileMinSize) {
                    continue
                }
                fun isIgnoreCase(path: String): Boolean {
                    ignorePaths?.forEach {
                        if (it.contains(path) || path.contains(it)) {
                            return true
                        }
                    }
                    return false
                }
                if (!isIgnoreCase(file.path)) allInfo.add(media)
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

    private fun getSelectionAndArgs(enumSet: EnumSet<MimeType>?): Pair<String, Array<String>> {
        val selection = StringBuilder()
        val args = ArrayList<String>()
        selection.append("(")
        val enumSets = mutableSetOf<Int>()
        enumSet?.forEach {
            if (!enumSets.contains(it.type)) enumSets.add(it.type)
        }
        enumSets.forEachIndexed { index, it ->
            if (index > 0) selection.append("OR ")
            when (it) {
                TYPE_IMG -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? ")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                }
                TYPE_VIDEO -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? ")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                }
            }
        }
        selection.append(") ")
        if (!enumSet.isNullOrEmpty()) {
            selection.append("AND (")
            var index = 1
            for (mime in enumSet) {
                selection.append(MediaStore.Files.FileColumns.MIME_TYPE).append("=? ")
                if (index != enumSet.size) {
                    selection.append("OR ")
                }
                index++
                args.add(mime.mMimeTypeName)
            }
            selection.append(") ")
        }
        return Pair(selection.toString(), args.toTypedArray())
    }
}
