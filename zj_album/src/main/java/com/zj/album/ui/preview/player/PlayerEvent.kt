package com.zj.album.ui.preview.player

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerView

/**
* @author ZJJ on 2019.10.24
* */
interface PlayerEvent {

    fun onLoading(path: String)

    fun onPrepare(path: String, videoSize: Long)

    fun onPlay(path: String)

    fun onPause(path: String)

    fun onStop(path: String)

    fun completing(path: String)

    fun onCompleted(path: String)

    fun onSeekChanged(seek: Int, fromUser: Boolean, videoSize: Long)

    fun getPlayerView(): PlayerView?

    fun getProgressInterval(): Long

    fun getContext(): Context?

    fun onError(e: Exception?)
}