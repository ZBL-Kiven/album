package com.zj.album.ui.preview.player

import android.net.Uri

/**
 * @author ZJJ on 2019.10.24
 *
 * return true to shutdown this event
 *
 * return false to continue event in default
 *
 * */
@Suppress("unused")
open class SimpleVideoEventListener {

    open fun onLoading(path: Uri?): Boolean {
        return false
    }

    open fun onPrepare(path: Uri?, videoSize: Long): Boolean {
        return false
    }

    open fun onPlay(path: Uri?): Boolean {
        return false
    }

    open fun onPause(path: Uri?): Boolean {
        return false
    }

    open fun onStop(path: Uri?): Boolean {
        return false
    }

    open fun onCompleting(path: Uri?): Boolean {
        return false
    }

    open fun onCompleted(path: Uri?): Boolean {
        return false
    }

    open fun onSeekChanged(seek: Int, fromUser: Boolean, videoSize: Long): Boolean {
        return false
    }

    open fun onError(e: Exception?): Boolean {
        return false
    }

}