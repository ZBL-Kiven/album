package com.zj.album.ui.preview.player

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

    open fun onLoading(path: String): Boolean {
        return false
    }

    open fun onPrepare(path: String, videoSize: Long): Boolean {
        return false
    }

    open fun onPlay(path: String): Boolean {
        return false
    }

    open fun onPause(path: String): Boolean {
        return false
    }

    open fun onStop(path: String): Boolean {
        return false
    }

    open fun onCompleted(path: String): Boolean {
        return false
    }

    open fun onSeekChanged(seek: Int, fromUser: Boolean, videoSize: Long): Boolean {
        return false
    }

    open fun onError(e: Exception?): Boolean {
        return false
    }

}