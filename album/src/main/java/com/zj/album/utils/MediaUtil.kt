package com.zj.album.utils

import android.content.Context
import com.zj.album.graphy.MediaLoaderTask
import com.zj.album.graphy.module.PhotoFileInfo

object MediaUtil {

    fun getRecentMediaUrl(context: Context, result: (String?) -> Unit) {
        MediaLoaderTask(0, object : MediaLoaderTask.MediaLoaderInterface {
            override fun onSuccess(photoFileInfos: ArrayList<PhotoFileInfo>) {
                if (photoFileInfos.size > 0)
                    result(photoFileInfos[0].topImgUri)
                else
                    result(null)
            }

            override fun onFailure() {
                result(null)
            }

        }).execute(context)
    }
}