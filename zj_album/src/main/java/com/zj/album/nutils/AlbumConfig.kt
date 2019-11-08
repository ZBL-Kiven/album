@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.zj.album.nutils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.widget.Toast
import com.zj.album.R
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.GraphDataHelper
import com.zj.album.nModule.OptionInfo
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect

/**
 * @author ZJJ on 2019.10.24
 * */
internal object AlbumConfig {

    private var options: OptionInfo? = null

    fun setOptions(appCtx: Application, options: OptionInfo) {
        appContext = appCtx
        this.options = options
        appName = options.appName
        maxSelectSize = options.maxSelectSize
        useOriginalPolymorphism = options.originalPolymorphism
        simultaneousSelection = options.simultaneousSelection
        pageTransformer = options.pagerEffect
        imageScaleEffect = options.imageScaleEffect
        DataProxy.setUseOriginal(options.useOriginDefault)
        loadData()
    }

    private fun initGraph(it: OptionInfo) {
        GraphDataHelper.init(it.mimeType, it.sortWithDesc, it.imgMinSize, it.imgMaxSize, it.videoMinSize, it.videoMaxSize, it.selectedUris, it.ignorePaths)
    }

    internal fun loadData() {
        options?.let { initGraph(it) }
    }

    @JvmStatic
    internal var appName = ""
        get() {
            return if (field.isEmpty()) getString(R.string.app_name) else field
        }
    @JvmStatic
    internal var maxSelectSize = Int.MAX_VALUE
    @JvmStatic
    internal var simultaneousSelection = false
    @JvmStatic
    internal var useOriginalPolymorphism = false
    @JvmStatic
    internal var pageTransformer: TransitionEffect = TransitionEffect.Zoom
    @JvmStatic
    internal var imageScaleEffect: ScaleEffect = ScaleEffect.CUBIC


    private var appContext: Context? = null

    @JvmStatic
    internal fun getString(id: Int, vararg args: Any): String {
        return appContext?.getString(id, *args) ?: ""
    }

    @JvmStatic
    internal fun getContentResolver(): ContentResolver? {
        return appContext?.contentResolver
    }

    internal fun toastLong(str: String) {
        Toast.makeText(appContext, str, Toast.LENGTH_LONG).show()
    }

    internal fun toastShort(str: String) {
        Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show()
    }

    internal fun toastLong(sid: Int, vararg args: Any) {
        val str = getString(sid, *args)
        Toast.makeText(appContext, str, Toast.LENGTH_LONG).show()
    }

    internal fun toastShort(sid: Int, vararg args: Any) {
        val str = getString(sid, *args)
        Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show()
    }

    internal fun clear() {
        appContext = null
        options = null
    }
}