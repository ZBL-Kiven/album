package com.zj.album.entity

import android.app.Activity
import android.content.Intent
import com.zj.album.MimeType

internal object SelectionSpec {

    var maxVideoDuration: Int = Integer.MAX_VALUE
    var maxVideoSize: Int = Integer.MAX_VALUE
    var maxVideoSelectable: Int = 0
    var maxImageSelectable: Int = 0
    var maxSelectable: Int = 1
    var mediaTypeExclusive = false
    var mimeTypes: Set<MimeType> = MimeType.ofAll()
    var onOpenCameraClick: ((Activity, Int) -> Unit)? = null
    var onResult: ((Intent?) -> Unit)? = null

    fun reset() {
        maxVideoDuration = Integer.MAX_VALUE
        maxVideoSize = Integer.MAX_VALUE
        maxVideoSelectable = 0
        maxImageSelectable = 0
        maxSelectable = 1
        mediaTypeExclusive = false
        mimeTypes = MimeType.ofAll()
        onOpenCameraClick = null
        onResult = null
    }
}