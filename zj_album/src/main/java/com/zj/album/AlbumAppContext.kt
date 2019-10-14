package com.zj.album

import android.app.Application

/**
 * 初始化全局的context
 */
object AlbumAppContext {

    /**
     * application 初始化的时候设置一个全局的application
     */
    var appContext:Application? = null
}