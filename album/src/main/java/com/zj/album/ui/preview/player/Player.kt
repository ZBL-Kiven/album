package com.zj.album.ui.preview.player

import com.zj.player.config.VideoConfig
import com.zj.player.z.ZController
import com.zj.player.z.ZRender
import com.zj.player.z.ZVideoPlayer

internal class Player internal constructor(runningName: String, controller: VideoController) : ZController<ZVideoPlayer, ZRender>(runningName, ZVideoPlayer(VideoConfig.create()), ZRender::class.java, controller) {

    companion object {
        fun build(runningName: String, controller: VideoController): Player {
            return Player(runningName, controller)
        }
    }

    fun full(isFull: Boolean): Boolean {
        return (getController() as? VideoController)?.full(isFull) ?: true
    }

    fun onFocus(hasFocus: Boolean) {
        (getController() as? VideoController)?.onFocus(hasFocus)
    }
}