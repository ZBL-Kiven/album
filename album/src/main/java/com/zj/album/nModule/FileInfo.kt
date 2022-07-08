package com.zj.album.nModule

import android.database.Cursor
import android.media.MediaMetadataRetriever
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
import java.lang.Exception

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
            val data = @Suppress("DEPRECATION") MediaStore.Files.FileColumns.DATA
            val uri = getFromCursor(cursor, data, "") { getString(it) }
            val id = getFromCursor(cursor, MediaStore.MediaColumns._ID, 0) { getInt(it) }
            val mime = getFromCursor(cursor, MediaStore.Files.FileColumns.MIME_TYPE, "") { getString(it) }
            val size = getFromCursor(cursor, MediaStore.Files.FileColumns.SIZE, 0) { getLong(it) }
            val fInfo = FileInfo(uri, mime, size, id)
            fInfo.duration = if (isVideo(mime) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getFromCursor(cursor, MediaStore.Video.Media.DURATION, 0) { getLong(it) }
            } else {
                try {
                    val rt = MediaMetadataRetriever()
                    rt.setDataSource(fInfo.path)
                    val duration = rt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    duration.toString().toLong()
                } catch (e: Exception) {
                    0
                }
            }
            return fInfo
        }

        private fun <T> getFromCursor(cursor: Cursor, index: String, default: T, func: Cursor.(column: Int) -> T): T {
            return runWithTryCatch {
                val column = cursor.getColumnIndex(index)
                if (column < 0) return@runWithTryCatch default
                func(cursor, column)
            } ?: default
        }
    }
}
