package com.zj.album.ui.preview.images

import android.view.View

interface OnPageChange<T : Any?> {

    fun onChange(data: T?, view: View)

    fun onDisplayChange(dataPosition: Int)

    fun onFocusChange(v: View, data: T?)
}