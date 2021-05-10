package com.zj.album.ui.preview.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ui.PlayerView

/**
* @author ZJJ on 2019.10.24
* */
interface PlayerEvent {

    fun onLoading(path: Uri?)

    fun onPrepare(path: Uri?, videoSize: Long)

    fun onPlay(path: Uri?)

    fun onPause(path: Uri?)

    fun onStop(path: Uri?)

    fun completing(path: Uri?)

    fun onCompleted(path: Uri?)

    fun onSeekChanged(seek: Int, fromUser: Boolean, videoSize: Long)

    fun getPlayerView(): PlayerView?

    fun getProgressInterval(): Long

    fun getContext(): Context?

    fun onError(e: Exception?)
}