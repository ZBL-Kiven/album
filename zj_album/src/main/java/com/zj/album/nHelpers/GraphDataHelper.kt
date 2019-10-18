package com.zj.album.nHelpers

import com.zj.album.MimeType
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object GraphDataHelper {

    private var isRunning = false

    fun init(
        enumSet: EnumSet<MimeType>?,
        useDesc: Boolean,
        minSize: Long,
        selectedPaths: Collection<Pair<String, Boolean>>? = null,
        ignorePaths: Array<String>? = arrayOf()
    ) {
        DataHelper.init(selectedPaths)
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
            val future = executors.submit(LocalDataExecute(enumSet, useDesc, minSize, ignorePaths))
            val data = future.get()
            if (!data.isNullOrEmpty()) DataHelper.setData(data)
        } else {
            isRunning = false
            executors?.shutdown()
            executors = Executors.newSingleThreadExecutor()
            initData(enumSet, useDesc, minSize, ignorePaths, executors)
        }
    }
}
