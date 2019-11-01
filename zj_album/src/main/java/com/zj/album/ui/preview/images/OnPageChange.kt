package com.zj.album.ui.preview.images

import android.view.View

/**
 * @author ZJJ on 2019.10.24
 * */
interface OnPageChange<T : Any?> {

    fun onBindData(data: T?, view: View)

    fun onFocusChange(v: View?, data: T?, focus: Boolean)

    fun onScrollStateChanged(interval: Float, state: Int)
}