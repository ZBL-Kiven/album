package com.zj.album.nutils

import android.content.Context
import android.os.Looper
import android.text.TextUtils

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import java.io.File

/**
 * Glide cache manager
 *
 * Created by ZJJ on 2019/11/5
 */
internal object GlideCacheUtil {

    /**
     * clear image cache form rom
     */
    private fun clearImageDiskCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable {
                    Glide.get(context).clearDiskCache()
                    // BusUtil.getBus().post(new GlideCacheClearSuccessEvent());
                }).start()
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * clear image cache form ram
     */
    private fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * clear all caches
     */
    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val imageExternalCatchDir = context.externalCacheDir?.toString() + ExternalPreferredCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
        deleteFolderFile(imageExternalCatchDir, true)
    }

    /**
     * deleted the file by path
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    for (file1 in files!!) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles()?.size == 0) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}