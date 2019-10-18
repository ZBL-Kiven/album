@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.zj.album

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zj.album.nHelpers.GraphDataHelper
import com.zj.album.nutils.Constance
import com.zj.album.ui.photograph.PhotoGraphActivity
import java.util.*

object PhotoAlbum {

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
        val b = Bundle()
        b.putInt(Constance.REQUEST_CODE, req)
        act.startActivityForResult(Intent(act, PhotoGraphActivity::class.java), req, b)
        GraphDataHelper.init(mimeType, sortWithDesc, minSize, selectedUris, ignorePaths)
    }

    var maxSelectSize = Int.MAX_VALUE
    var useOriginDefault = false

    private var appContext: Context? = null

    fun getString(id: Int): String {
        return appContext?.getString(id) ?: ""
    }

    fun getContentResolver(): ContentResolver? {
        return appContext?.contentResolver
    }

    fun ofImage(): EnumSet<MimeType> {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF)
    }

    fun ofStaticImage(): EnumSet<MimeType> {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG)
    }

    fun ofVideo(): EnumSet<MimeType> {
        return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF)
    }

    fun ofAll(): EnumSet<MimeType> {
        return EnumSet.allOf(MimeType::class.java)
    }

    fun pairOf(vararg types: MimeType): EnumSet<MimeType> {
        return EnumSet.copyOf(setOf(*types))
    }

    fun pairOf(vararg types: EnumSet<MimeType>): EnumSet<MimeType>? {
        if (types.isEmpty()) return null
        var requestSet: EnumSet<MimeType>? = null
        types.forEach {
            requestSet?.addAll(it) ?: { requestSet = EnumSet.copyOf(it) }.invoke()
        }
        return requestSet
    }
}