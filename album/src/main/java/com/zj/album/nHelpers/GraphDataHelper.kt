package com.zj.album.nHelpers

import android.os.Handler
import android.os.Looper
import com.zj.album.nModule.SimpleSelectInfo
import com.zj.album.nutils.MimeType
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

internal object GraphDataHelper {

    var isRunning = false; private set

    fun init(enumSet: EnumSet<MimeType>?, useDesc: Boolean, imgMinSize: Long, imgMaxSize: Long, vMinSize: Long, vMaxSize: Long, selectedPaths: Collection<SimpleSelectInfo>? = null, ignorePaths: ArrayList<String>?) {
        DataProxy.init(selectedPaths)
        initData(enumSet, useDesc, imgMinSize, imgMaxSize, vMinSize, vMaxSize, ignorePaths)
    }

    private fun initData(enumSet: EnumSet<MimeType>?, useDesc: Boolean, imgMinSize: Long, imgMaxSize: Long, vMinSize: Long, vMaxSize: Long, ignorePaths: List<String>? = arrayListOf(), e: ExecutorService? = null) {
        if (isRunning) return
        var executors = e
        isRunning = true
        if (executors != null && !executors.isShutdown && !executors.isTerminated) {
            executors.execute(LocalDataExecute(enumSet, useDesc, imgMinSize, imgMaxSize, vMinSize, vMaxSize, ignorePaths) {
                Handler(Looper.getMainLooper()).post {
                    isRunning = false
                    DataProxy.setLocalData(it)
                }
            })
        } else {
            isRunning = false
            executors?.shutdown()
            executors = Executors.newSingleThreadExecutor()
            initData(enumSet, useDesc, imgMinSize, imgMaxSize, vMinSize, vMaxSize, ignorePaths, executors)
        }
    }
}
