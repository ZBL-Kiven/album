package com.zj.album.ui.preview.player


import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.zj.album.nutils.runWithTryCatch
import java.io.File
import kotlin.math.min

//Video player module
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ExoPlayer(private val event: PlayerEvent?) {

    private var player: SimpleExoPlayer? = null
    private var playPath: String = ""
    private var duration = 0L
    private var curState: State = State.DESTROY
    private var handler: Handler? = null

    internal enum class State(val pri: Int) {
        PREPARE(3), RESUME(4), STOP(2), PAUSE(1), DESTROY(0)
    }

    private val runnable: Runnable = Runnable {
        if (isPrepared) {
            val curDuration = getCurrentProgress()
            val curSeekProgress = (curDuration * 1.0f / duration * 100 + 0.5f).toInt()
            event?.onSeekChanged(curSeekProgress, false)
            startProgressListen()
        }
    }

    private val isPrepared: Boolean; get() = curState.pri >= State.PREPARE.pri


    fun isStop(): Boolean {
        return curState.pri < State.PREPARE.pri
    }

    fun isResumed(): Boolean {
        return curState.pri == State.RESUME.pri
    }

    private var onPaused = true
    private var onResumed = false

    fun initData(path: String) {
        curState = State.STOP
        event?.onLoading(path)
        handler = Handler(Looper.getMainLooper())
        playPath = path
        val uri = Uri.fromFile(File(path))
        val context = event?.getContext() ?: return
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.applicationContext?.packageName), DefaultBandwidthMeter())
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        event.getPlayerView()?.player = player
        player?.addListener(eventListener)
        player?.prepare(videoSource)
    }

    fun resume() {
        runWithPlayer {
            if (it.currentPosition >= it.duration) {
                seekTo(0, false)
            }
            it.playWhenReady = true
        }
        curState = State.RESUME
        event?.onPlay(playPath)
        startProgressListen()
    }

    fun pause() {
        curState = State.PAUSE
        runWithPlayer { it.playWhenReady = false }
        event?.onPause(playPath)
        stopProgressListen()
    }

    fun seekTo(progress: Int, fromUser: Boolean) {
        if (curState != State.PAUSE) pause()
        val seekProgress = (min(100, progress) / 100f * getDuration() - 1).toLong()
        runWithPlayer { it.seekTo(seekProgress) }
        event?.onSeekChanged(progress, fromUser)
    }

    fun getDuration(): Long {
        return duration
    }

    fun getCurrentProgress(): Long {
        return runWithPlayer { it.currentPosition } ?: 0L
    }

    fun release() {
        curState = State.DESTROY
        stopProgressListen()
        handler = null
        runWithPlayer { it.release() }
    }

    private fun <T> runWithPlayer(block: (SimpleExoPlayer) -> T?): T? {
        return player?.let {
            return runWithTryCatch { block(it) }
        }
    }

    private val eventListener = object : PlayerEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    curState = State.STOP
                    event?.onCompleted(playPath)
                }
                Player.STATE_READY -> {
                    if (!isPrepared) {
                        curState = State.PREPARE
                        duration = player?.duration ?: 0L
                        event?.onPrepare(playPath)
                    }
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            curState = State.STOP
            event?.onError(error)
        }
    }

    private fun startProgressListen() {
        handler?.postDelayed(runnable, event?.getProgressPrevios() ?: 100)
    }

    private fun stopProgressListen() {
        handler?.removeCallbacksAndMessages(null)
    }
}