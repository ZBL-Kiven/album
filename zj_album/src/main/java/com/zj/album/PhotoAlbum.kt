@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.zj.album

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.GraphDataHelper
import com.zj.album.ui.photograph.PhotoGraphActivity

object PhotoAlbum {

    fun options(act: Activity, requestCode: Int): Options {
        this.appContext = act.applicationContext
        return Options(requestCode) {
            this.options = it
            this.maxSelectSize = it.maxSelectSize
            DataProxy.setUseOriginal(it.useOriginDefault)
            this.simultaneousSelection = it.simultaneousSelection
            act.startActivityForResult(Intent(act, PhotoGraphActivity::class.java), it.req)
            loadData()
        }
    }

    private fun initGraph(it: Options) {
        GraphDataHelper.init(it.mimeType, it.sortWithDesc, it.minSize, it.selectedUris, it.ignorePaths)
    }

    private var options: Options? = null

    internal fun loadData() {
        options?.let { initGraph(it) }
    }

    @JvmStatic
    internal var maxSelectSize = Int.MAX_VALUE
    @JvmStatic
    internal var simultaneousSelection = false

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