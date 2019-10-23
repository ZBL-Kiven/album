package com.zj.album.ui.preview.helper


import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.*

//视频播放
class MediaPlayerHelper(context: Context) {


    private val bandwidthMeter: DefaultBandwidthMeter = DefaultBandwidthMeter()

    private val trackSelector: DefaultTrackSelector =
        DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

    private lateinit var player: SimpleExoPlayer

    private lateinit var listener: VideoPlayEventListener

    private var voicePlayManager: VoicePlayManager

    enum class Status {
        Start, Resume, SeekTo, Pause, End
    }

    init {
        this.voicePlayManager = VoicePlayManager(context)
    }

    /**
     * 准备资源
     * @param playerView 进行视频播放 view
     * @param uri 视频的 uri
     * @param listener 播放中事件的回调
     */
    fun ready(playerView: PlayerView, uri: Uri, listener: VideoPlayEventListener) {
        val context = playerView.context
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "SunPeople"),
            bandwidthMeter
        )
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

        this.listener = listener
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        playerView.player = player
        player.prepare(videoSource)
        player.addListener(simpleExoEventListener)
    }

    /**
     * 启动播放
     */
    fun start() {
        if (!checkPlayerInitialized()) return
        voicePlayManager.requestAudioFocus()
        player.playWhenReady = true
        listener.onStatusChange(Status.Start)
    }

    /**
     * 用于暂停后恢复播放
     */
    fun resume() {
        if (!checkPlayerInitialized()) return
        voicePlayManager.requestAudioFocus()
        if (player.currentPosition >= player.duration) {
            seekTo(0)
        }
        player.playWhenReady = true
        listener.onStatusChange(Status.Resume)
    }

    /**
     * 暂停
     */
    fun pause() {
        if (!checkPlayerInitialized()) return
        voicePlayManager.abandonAudioFocus()
        player.playWhenReady = false
        listener.onStatusChange(Status.Pause)
    }

    /**
     * 跳转到指定的进度
     */
    fun seekTo(progress: Int) {
        if (!checkPlayerInitialized()) return
        voicePlayManager.requestAudioFocus()
        player.seekTo(progress.toLong())
        listener.onStatusChange(Status.SeekTo)
    }

    /**
     * 得到当前的进度
     */
    fun getCurrentProgress(): Int {
        if (!checkPlayerInitialized()) return 0
        return player.currentPosition.toInt()
    }

    /**
     * 释放资源
     */
    fun release() {
        if (!checkPlayerInitialized()) return
        voicePlayManager.abandonAudioFocus()
        player.release()
        timer?.cancel()
    }

    private fun checkPlayerInitialized(): Boolean {
        return this::player.isInitialized
    }

    /**
     * 第三方播放器的播放事件回调
     */
    private val simpleExoEventListener = object : SimpleExoEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    voicePlayManager.abandonAudioFocus()
                    listener.onStatusChange(Status.End)
                    timer?.cancel()
                }
                Player.STATE_READY -> postProgress()
            }
        }
    }

    private var timer: Timer? = null

    /**
     * 开始通过接口发送当前所处的进度
     */
    private fun postProgress() {
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    listener.onProgress(
                        player.currentPosition.toInt(),
                        player.duration.toInt()
                    )
                }
            }
        }, 0, 500)
    }

    interface VideoPlayEventListener {
        fun onProgress(currentProgress: Int, maxProgress: Int)
        fun onStatusChange(status: Status)
    }

    open class SimpleExoEventListener : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

        }

        override fun onSeekProcessed() {

        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {

        }

        override fun onPlayerError(error: ExoPlaybackException?) {

        }

        override fun onLoadingChanged(isLoading: Boolean) {

        }

        override fun onPositionDiscontinuity(reason: Int) {

        }

        override fun onRepeatModeChanged(repeatMode: Int) {

        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        }
    }
}
