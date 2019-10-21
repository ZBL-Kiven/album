package com.zj.album.nModule

import android.database.Cursor
import android.provider.MediaStore
import com.zj.album.isVideo
import com.zj.album.nHelpers.DataStore
import com.zj.album.nutils.runWithTryCatch
import java.io.Serializable

/*
* Created by ZJJ on 2019/10/16
* */
data class FileInfo(
    val parentFolderPath: String?,
    val path: String,
    val mimeType: String,
    var size: Long,
    internal var useOriginal: Boolean = false
) : Serializable {
    private var lastModifyTs: Long = 0

    var duration: Long = 0
        internal set(value) {
            field = value
        }
    var isSelected: Boolean = false
        internal set(value) {
            if (value != field) {
                field = value
                lastModifyTs = if (value) System.currentTimeMillis() else 0
                DataStore.onSelectedChanged(this)
            }
        }

    companion object {

        fun valueOf(cursor: Cursor): FileInfo {
            val uri = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH))
            } ?: ""
            val parent = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT))
            } ?: ""
            val mime = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
            } ?: ""
            val size = runWithTryCatch {
                cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
            } ?: 0
            val fInfo = FileInfo(parent, uri, mime, size)
            if (isVideo(mime)) {
                fInfo.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
            }
            return fInfo
        }
    }
}
