package com.zj.album.nModule

import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.DataStore
import com.zj.album.nutils.isGif
import com.zj.album.nutils.isImage
import com.zj.album.nutils.isVideo
import com.zj.album.nutils.runWithTryCatch
import java.io.Serializable

/**
 * @author ZJJ on 2019.10.24
 * */
@Suppress("unused")
data class FileInfo internal constructor(val path: String, val mimeType: String, var size: Long, var mediaColumnId: Int, var useOriginalImages: Boolean = false) : Serializable {
    var lastModifyTs: Long = 0; private set
    var duration: Long = 0; internal set
    val isVideo: Boolean; get() = isVideo(mimeType)
    val isImage: Boolean; get() = isImage(mimeType)
    val isGif: Boolean; get() = isGif(mimeType)

    fun getContentUri(): Uri {
        val mediaPath = if (isVideo) "video" else "images"
        val baseUri = Uri.parse("content://media/external/${mediaPath}/media")
        return Uri.withAppendedPath(baseUri, "$mediaColumnId")
    }

    internal fun isSelected(): Boolean {
        return DataStore.isSelected(path)
    }

    internal fun setSelected(selected: Boolean, ignoreMaxCount: Boolean = false): Boolean {
        if (selected == isSelected()) return false
        lastModifyTs = if (DataProxy.onSelectedChanged(selected, this, ignoreMaxCount)) {
            if (selected) System.currentTimeMillis()
            return true
        } else 0
        return false
    }

    internal fun setOriginal(useOriginal: Boolean): Boolean {
        return DataProxy.onOriginalChanged(useOriginal, path)
    }

    internal fun isOriginal(): Boolean {
        return DataStore.isOriginalData(path)
    }

    internal companion object {

        fun valueOf(cursor: Cursor): FileInfo {
            @Suppress("DEPRECATION") val uri = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            } ?: ""
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val mime = runWithTryCatch {
                cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
            } ?: ""
            val size = runWithTryCatch {
                cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
            } ?: 0
            val fInfo = FileInfo(uri, mime, size, id)
            if (isVideo(mime) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                fInfo.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
            }
            return fInfo
        }
    }
}
