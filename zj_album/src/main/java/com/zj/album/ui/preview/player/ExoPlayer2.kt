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
import com.zj.album.nutils.log
import com.zj.album.nutils.runWithTryCatch
import java.io.File
import kotlin.math.max
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ExoPlayer2(private var event: PlayerEvent?) {

    private var player: SimpleExoPlayer? = null
    private var playPath: String = ""
    private var duration = 0L
    private var handler: Handler? = null

    private var autoPlay = false
    private var isReady = false

    internal enum class State(val pri: Int) {
        PLAY(6), READY(5), LOADING(4), COMPLETED(3), PAUSE(2), STOP(1), DESTROY(0)
    }

    fun isReady(): Boolean {
        return curState.pri >= State.READY.pri
    }

    fun isPlaying(): Boolean {
        return curState.pri >= State.PLAY.pri
    }

    fun isPause(): Boolean {
        return curState.pri <= State.PAUSE.pri
    }

    fun isStop(): Boolean {
        return curState.pri <= State.STOP.pri
    }

    fun isDestroyed(): Boolean {
        return curState.pri == State.DESTROY.pri
    }

    fun currentPlayPath(): String {
        return playPath
    }

    private val runnable: Runnable = Runnable {
        if (isReady()) {
            val curDuration = player?.currentPosition ?: 0
            if (curDuration > 0) {
                val interval = curDuration * 1.0f / max(1, duration)
                val curSeekProgress = (interval * 100 + 0.5f).toInt()
                event?.onSeekChanged(curSeekProgress, false, duration)
                if (interval >= 0.99f) {
                    event?.completing(currentPlayPath())
                }
            }
            startProgressListen()
        } else {
            stopProgressListen()
        }
    }

    private var curState: State = State.DESTROY
        set(value) {
            if (field == value) return
            when (value) {
                State.LOADING -> {
                    isReady = false
                    autoPlay(false)
                    if (isPlaying()) {
                        setPlayerState(State.STOP)
                    }
                    event?.onLoading(playPath)
                    loadingData()
                }
                State.READY -> {
                    duration = player?.duration ?: 0
                    event?.onPrepare(playPath, duration)
                    isReady = true
                    if (autoPlay) {
                        player?.playWhenReady
                        setPlayerState(State.PLAY)
                        return
                    }
                }
                State.PLAY -> {
                    runWithPlayer {
                        if (it.currentPosition >= it.duration) {
                            seekTo(0, false)
                        }
                        field = value
                        it.playWhenReady = true
                    }
                    startProgressListen()
                    event?.onPlay(playPath)
                    return
                }

                State.COMPLETED -> {
                    setPlayerState(State.STOP)
                }

                State.PAUSE -> {
                    if (isPause()) return
                    runWithPlayer { it.playWhenReady = false }
                    stopProgressListen()
                    event?.onPause(playPath)
                }

                State.STOP -> {
                    if (isStop()) return
                    if (isPlaying()) setPlayerState(State.PAUSE)
                    isReady = false
                    autoPlay(false)
                    resetAndStop(true)
                }

                State.DESTROY -> {
                    if (isDestroyed()) return
                    if (isPlaying()) setPlayerState(State.PAUSE)
                    if (!isStop()) setPlayerState(State.STOP)
                    event = null
                }
            }
            field = value
        }

    private fun autoPlay(autoPlay: Boolean) {
        this.autoPlay = autoPlay
    }

    private fun loadingData() {
        event?.onLoading(playPath)
        handler = Handler(Looper.getMainLooper())
        val uri = Uri.fromFile(File(playPath))
        val context = event?.getContext() ?: return
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.applicationContext?.packageName), DefaultBandwidthMeter())
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        runWithPlayer {
            event?.getPlayerView()?.player = it
            it.addListener(eventListener)
            it.prepare(videoSource)
        }
    }

    private fun resetAndStop(notifyStop: Boolean = false) {
        runWithPlayer {
            it.removeListener(eventListener)
            it.release()
        }
        if (notifyStop) event?.onStop(playPath)
        handler = null
        player = null
        playPath = ""
    }

    private fun setPlayerState(state: State) {
        if (isReady() && state == State.READY) return
        log("cur = $curState   s = $state")
        synchronized(this.curState) {
            this.curState = state
        }
    }

    fun setData(path: String) {
        playPath = path
        setPlayerState(State.LOADING)
    }

    fun play() {
        if (isReady) {
            setPlayerState(State.PLAY)
        } else {
            autoPlay(true)
        }
    }

    fun pause() {
        setPlayerState(State.PAUSE)
    }

    fun stop() {
        autoPlay(false)
        setPlayerState(State.STOP)
    }

    fun seekTo(progress: Int, fromUser: Boolean) {
        if (curState != State.PAUSE) setPlayerState(State.PAUSE)
        autoPlay(false)
        if (duration > 0) {
            val seekProgress = (min(100, progress) / 100f * duration - 1).toLong()
            runWithPlayer { it.seekTo(seekProgress) }
            event?.onSeekChanged(progress, fromUser, duration)
        }
    }

    fun release() {
        setPlayerState(State.DESTROY)
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
                    setPlayerState(State.COMPLETED)
                }
                Player.STATE_READY -> {
                    setPlayerState(State.READY)
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            setPlayerState(State.STOP)
            event?.onError(error)
        }
    }

    private fun startProgressListen() {
        handler?.postDelayed(runnable, event?.getProgressInterval() ?: 100)
    }

    private fun stopProgressListen() {
        handler?.removeCallbacksAndMessages(null)
    }
}