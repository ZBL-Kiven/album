@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.zj.album

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.zj.album.nHelpers.GraphDataHelper
import com.zj.album.ui.photograph.PhotoGraphActivity
import java.util.*

object PhotoAlbum {

    @JvmStatic
    fun startPhotoGraphActivity(
        act: Activity,
        req: Int,
        selectedUris: Collection<Pair<String, Boolean>>?,
        maxSelectSize: Int = Int.MAX_VALUE,
        mimeType: EnumSet<MimeType>? = pairOf(ofImage(), ofVideo()),
        useOriginDefault: Boolean = false,
        ignorePaths: Array<String>? = arrayOf(),
        minSize: Long,
        sortWithDesc: Boolean = false
    ) {
        this.appContext = act.applicationContext
        this.maxSelectSize = maxSelectSize
        this.useOriginDefault = useOriginDefault
        this.requestCode = req
        act.startActivityForResult(Intent(act, PhotoGraphActivity::class.java), req)
        GraphDataHelper.init(mimeType, sortWithDesc, minSize, selectedUris, ignorePaths)
    }

    @JvmStatic
    var requestCode: Int = 0
    @JvmStatic
    var maxSelectSize = Int.MAX_VALUE
    @JvmStatic
    var useOriginDefault = false

    private var appContext: Context? = null
    @JvmStatic
    fun getString(id: Int, vararg args: Any): String {
        return appContext?.getString(id, *args) ?: ""
    }

    @JvmStatic
    fun getContentResolver(): ContentResolver? {
        return appContext?.contentResolver
    }

    fun toastLong(str: String) {
        Toast.makeText(appContext, str, Toast.LENGTH_LONG).show()
    }

    fun toastShort(str: String) {
        Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun ofImage(): EnumSet<MimeType> {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF)
    }

    @JvmStatic
    fun ofStaticImage(): EnumSet<MimeType> {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG)
    }

    @JvmStatic
    fun ofVideo(): EnumSet<MimeType> {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF)
    }

    @JvmStatic
    fun ofAll(): EnumSet<MimeType> {
        return EnumSet.allOf(MimeType::class.java)
    }

    @JvmStatic
    fun pairOf(vararg types: MimeType): EnumSet<MimeType> {
        return EnumSet.copyOf(setOf(*types))
    }

    @JvmStatic
    fun pairOf(vararg types: EnumSet<MimeType>): EnumSet<MimeType>? {
        if (types.isEmpty()) return null
        var requestSet: EnumSet<MimeType>? = null
        types.forEach {
            requestSet?.addAll(it) ?: { requestSet = EnumSet.copyOf(it) }.invoke()
        }
        return requestSet
    }
}