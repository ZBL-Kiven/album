package com.zj.album.graphy

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.MediaStore
import android.support.v4.util.Pair
import android.util.Log
import com.zj.album.R
import com.zj.album.entity.SelectionSpec
import com.zj.album.graphy.activity.PhotoGraphActivity.*
import com.zj.album.graphy.module.LocalMedia
import com.zj.album.graphy.module.PhotoFileInfo
import com.zj.album.services.Constancs
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * update by xiaojie on 2019/08/20.
 * 异步从本地文件夹获取Android的所有图片
 */
internal class MediaLoaderTask(private val findType: Int, private val mediaInterface: MediaLoaderInterface?) : AsyncTask<Context, Int, ArrayList<PhotoFileInfo>>() {
    private val SEARCH_URI = MediaStore.Files.getContentUri("external")
    private val PROJECTION = arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE, MediaStore.Video.VideoColumns.DURATION)
    private val isDesc = Constancs.MediaSortDesc

    private val filterMime = SelectionSpec.mimeTypes.map { it.mMimeTypeName }.toTypedArray()
    private val pathIndexes = ArrayList<Int>()

    private val sortOrder: String
        get() = MediaStore.Files.FileColumns.DATE_MODIFIED + if (isDesc) " DESC " else " ASC "

    //处理 type 条件
    // 图片
    // 视频
    // 图片跟视频
    //处理 mime 条件
    //处理 parentIndex 条件
    private val selectionAndArgs: Pair<String, Array<String>>
        get() {
            val selection = StringBuilder()
            val args = ArrayList<String>()
            selection.append("(")
            when (findType) {
                IMAGES -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? ")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                }
                VIDEOS -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? ")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                }
                IMAGES_AND_VIDEOS -> {
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? ")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                    selection.append("OR ")
                    selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? ")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                }
            }
            selection.append(") ")
            if (filterMime.isNotEmpty()) {
                selection.append("AND (")
                var index = 1
                for (mime in filterMime) {
                    selection.append(MediaStore.Files.FileColumns.MIME_TYPE).append("=? ")
                    if (index != filterMime.size) {
                        selection.append("OR ")
                    }
                    index++
                    args.add(mime)
                }
                selection.append(") ")
            }
            if (pathIndexes.isNotEmpty()) {
                selection.append("AND (")
                var index = 1
                for (pathIndex in pathIndexes) {
                    if (pathIndex < 0) {
                        continue
                    }

                    selection.append(MediaStore.Files.FileColumns.PARENT).append("=? ")
                    if (index != pathIndexes.size) {
                        selection.append("OR ")
                    }
                    index++
                    args.add(pathIndex.toString())
                }
                selection.append(") ")
            }

            return Pair(selection.toString(), args.toTypedArray())
        }

    override fun doInBackground(vararg contexts: Context): ArrayList<PhotoFileInfo>? {
        var mCursor: Cursor? = null
        try {
            val t1 = System.currentTimeMillis()
            val resolver = contexts[0].contentResolver
            val selectionAndArgs = selectionAndArgs
            mCursor = resolver.query(SEARCH_URI, PROJECTION, selectionAndArgs.first,
                    selectionAndArgs.second, sortOrder + getRange(0, 0))
            if (mCursor == null || isCancelled) {
                return null
            }
            val t2 = System.currentTimeMillis()
            Log.e("time-doInBackground", "       " + (t2 - t1))
            return subGroupOfImage(mCursor, contexts[0])
        } catch (e: Exception) {
            Log.e(TAG, "相册读取失败", e)
        } finally {
            mCursor?.close()
        }
        return null
    }

    override fun onPostExecute(photoFileInfos: ArrayList<PhotoFileInfo>?) {
        if (mediaInterface != null) {
            if (photoFileInfos != null) {
                mediaInterface.onSuccess(photoFileInfos)
            } else {
                mediaInterface.onFailure()
            }
        }
    }

    /**
     * 组装分组界面GridView的数据源，因扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     */
    private fun subGroupOfImage(cursor: Cursor, context: Context): ArrayList<PhotoFileInfo>? {
        val mGroupMap = HashMap<String, ArrayList<LocalMedia>>()
        val allInfo = PhotoFileInfo()
        val t1 = System.currentTimeMillis()
        while (cursor.moveToNext()) {
            if (isCancelled) return null
            val media = LocalMedia.valueOf(cursor)
            val file = File(media.uri)
            if (file.exists() && file.isFile && file.length() <= FILE_MIN_SIZE) {
                continue
            }
            allInfo.localMedias.add(media)
            val folderName = file.parentFile.name
            if (mGroupMap.containsKey(folderName)) {
                mGroupMap[folderName]?.add(media)
            } else {
                mGroupMap[folderName] = arrayListOf(media)
            }
        }
        val t2 = System.currentTimeMillis()
        Log.e("time-while1", "       " + (t2 - t1))
        if (mGroupMap.size == 0 || isCancelled) {
            return null
        }
        val list = ArrayList<PhotoFileInfo>()
        val iterator = mGroupMap.entries.iterator()
        while (iterator.hasNext() && !isCancelled) {
            if (isCancelled) return null
            val entry = iterator.next()
            val info = PhotoFileInfo()
            val key = entry.key
            val value = entry.value
            info.parentPath = key
            info.imageCounts = value.size
            info.topImgUri = value[0].fileUri//获取该组的第一张图片
            info.localMedias = value
            list.add(info)
        }
        val t3 = System.currentTimeMillis()
        Log.e("time-while2", "       " + (t3 - t2))
        allInfo.imageCounts = allInfo.localMedias.size
        allInfo.topImgUri = allInfo.localMedias[0].fileUri
        allInfo.parentPath = context.getString(R.string.im_all)
        list.add(0, allInfo)
        return if (isCancelled) null else list
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

    interface MediaLoaderInterface {
        fun onSuccess(photoFileInfos: ArrayList<PhotoFileInfo>)

        fun onFailure()
    }

    companion object {

        /**
         * 去掉这个筛选，和 SP 一致
         */
        private val FILE_MIN_SIZE = 1 // 最小图片大小要大于10K

        private val TAG = MediaLoaderTask::class.java.simpleName
    }
}
