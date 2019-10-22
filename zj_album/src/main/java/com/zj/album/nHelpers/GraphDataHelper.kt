package com.zj.album.nHelpers

import android.os.Handler
import android.os.Looper
import com.zj.album.MimeType
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object GraphDataHelper {

    var isRunning = false; private set

    fun init(
        enumSet: EnumSet<MimeType>?,
        useDesc: Boolean,
        minSize: Long,
        selectedPaths: Collection<Pair<String, Boolean>>? = null,
        ignorePaths: Array<String>? = arrayOf()
    ) {
        DataProxy.init(selectedPaths)
        initData(enumSet, useDesc, minSize, ignorePaths)
    }

    private fun initData(
        enumSet: EnumSet<MimeType>?,
        useDesc: Boolean,
        minSize: Long,
        ignorePaths: Array<String>? = arrayOf(),
        e: ExecutorService? = null
    ) {
        if (isRunning) return
        var executors = e
        isRunning = true
        if (executors != null && !executors.isShutdown && !executors.isTerminated) {
            executors.execute(LocalDataExecute(enumSet, useDesc, minSize, ignorePaths) {
                Handler(Looper.getMainLooper()).post {
                    isRunning = false
                    DataProxy.setLocalData(it)
                }
            })
        } else {
            isRunning = false
            executors?.shutdown()
            executors = Executors.newSingleThreadExecutor()
            initData(enumSet, useDesc, minSize, ignorePaths, executors)
        }
    }
}
