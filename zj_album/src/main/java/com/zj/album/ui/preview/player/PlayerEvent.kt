package com.zj.album.ui.preview.player

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerView

interface PlayerEvent {

    fun onLoading(path: String)

    fun onPrepare(path: String)

    fun onPlay(path: String)

    fun onPause(path: String)

    fun onCompleted(path: String)

    fun onSeekChanged(seek: Int, fromUser: Boolean)

    fun getPlayerView(): PlayerView?

    fun getProgressPrevios(): Long

    fun getContext(): Context?

    fun onError(e: Exception?)
}