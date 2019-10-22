package com.zj.album.nModule

import android.database.Cursor
import android.provider.MediaStore
import com.zj.album.isVideo
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.DataStore
import com.zj.album.nutils.runWithTryCatch
import java.io.Serializable

/*
* Created by ZJJ on 2019/10/16
* */
data class FileInfo(val path: String, val mimeType: String, var size: Long) : Serializable {
    var lastModifyTs: Long = 0; private set

    var duration: Long = 0
        internal set(value) {
            field = value
        }

    val isVideo: Boolean; get() = isVideo(mimeType)

    internal fun isSelected(): Boolean {
        return DataStore.isSelected(path)
    }

    internal fun setSelected(selected: Boolean, ignoreMaxCount: Boolean = false): Boolean {
        lastModifyTs = if (DataProxy.onSelectedChanged(selected, this, ignoreMaxCount)) {
            if (selected) System.currentTimeMillis()
            return true
        } else 0
        return false
    }

    companion object {

        fun valueOf(cursor: Cursor): FileInfo {
            @Suppress("DEPRECATION") val uri = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            } ?: ""
            val mime = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
            } ?: ""
            val size = runWithTryCatch {
                cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
            } ?: 0
            val fInfo = FileInfo(uri, mime, size)
            if (isVideo(mime)) {
                fInfo.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
            }
            return fInfo
        }
    }
}
