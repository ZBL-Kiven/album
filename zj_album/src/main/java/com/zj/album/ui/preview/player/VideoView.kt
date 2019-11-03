package com.zj.album.ui.preview.player

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.exoplayer2.ui.PlayerView
import com.zj.album.R
import com.zj.album.nutils.getDuration
import com.zj.album.ui.views.BaseLoadingView

/**
 * @author ZJJ on 2019.10.24
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class VideoView : FrameLayout, PlayerEvent {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        init()
        initListener()
    }

    private var vPlay: View? = null
    private var tvStart: TextView? = null
    private var tvEnd: TextView? = null
    private var seekBar: SeekBar? = null
    private var loadingView: BaseLoadingView? = null
    private var bottomToolsBar: View? = null
    private var videoPlayerView: PlayerView? = null
    private var isTickingSeekBarFromUser: Boolean = false
    private var autoPlay = false
    private var seekProgressInterval: Long = 16

    private var simpleVideoEventListener: SimpleVideoEventListener? = null
    private var exoPlayer: ExoPlayer? = null

    private fun init() {
        val root = View.inflate(context, R.layout.preview_video, this)
        videoPlayerView = root.findViewById(R.id.video_preview_PlayerView)
        vPlay = root.findViewById(R.id.video_preview_iv_play)
        tvStart = root.findViewById(R.id.video_preview_tv_start)
        tvEnd = root.findViewById(R.id.video_preview_tv_end)
        loadingView = root.findViewById(R.id.video_preview_loading)
        bottomToolsBar = root.findViewById(R.id.video_preview__tools_bar)
        seekBar = root.findViewById(R.id.video_preview_sb)
        videoPlayerView?.useController = false
        exoPlayer = ExoPlayer(this)
    }

    private fun initListener() {
        vPlay?.setOnClickListener {
            it.isEnabled = false
            if (!it.isSelected) {
                exoPlayer?.play()
            } else {
                exoPlayer?.pause()
            }
            it.isEnabled = true
        }
    }

    private fun initSeekBar() {
        seekBar?.isEnabled = false
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (isTickingSeekBarFromUser && p2) {
                    exoPlayer?.seekTo((p0?.progress) ?: 0, true)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isTickingSeekBarFromUser = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isTickingSeekBarFromUser = false
                exoPlayer?.play()
            }
        })
    }

    override fun onLoading(path: String) {
        seekBar?.isEnabled = false
        if (simpleVideoEventListener?.onLoading(path) == false) {
            if (!autoPlay) loadingView?.setMode(BaseLoadingView.DisplayMode.loading)
        }
    }

    override fun onPrepare(path: String, videoSize: Long) {
        seekBar?.isEnabled = true
        if (simpleVideoEventListener?.onPrepare(path, videoSize) == false) {
            if (!autoPlay) loadingView?.setMode(BaseLoadingView.DisplayMode.normal)
            tvEnd?.text = getDuration(videoSize)
            if (autoPlay) exoPlayer?.play()
        }
    }

    override fun onPlay(path: String) {
        seekBar?.isSelected = true
        seekBar?.isEnabled = true
        if (simpleVideoEventListener?.onPlay(path) == false) {
            isTickingSeekBarFromUser = false
            showOrHidePlayBtn(false)
        }
    }

    override fun onPause(path: String) {
        seekBar?.isSelected = false
        if (simpleVideoEventListener?.onPause(path) == false) {
            showOrHidePlayBtn(true)
        }
    }

    override fun onStop(path: String) {
        seekBar?.isSelected = false
        seekBar?.isEnabled = false
        onSeekChanged(0, false, 0)
        if (simpleVideoEventListener?.onStop(path) == false) {
            showOrHidePlayBtn(true)
        }
    }

    override fun completing(path: String) {
        if (simpleVideoEventListener?.onCompleting(path) == false) {
            showOrHidePlayBtn(true)
        }
    }

    override fun onCompleted(path: String) {
        if (simpleVideoEventListener?.onCompleted(path) == false) {
            seekBar?.isSelected = false
            seekBar?.isEnabled = false
            onSeekChanged(0, false, 0)
        }
    }

    override fun onSeekChanged(seek: Int, fromUser: Boolean, videoSize: Long) {
        if (!isTickingSeekBarFromUser && !fromUser) {
            seekBar?.progress = seek
        }
        if (simpleVideoEventListener?.onSeekChanged(seek, fromUser, videoSize) == false) {
            val startProgress = videoSize / 100f * seek
            tvStart?.text = getDuration(startProgress.toLong())
        }
    }

    override fun onError(e: Exception?) {
        if (simpleVideoEventListener?.onError(e) == false) {
            loadingView?.setMode(BaseLoadingView.DisplayMode.noData)
        }
    }

    override fun getPlayerView(): PlayerView? {
        return videoPlayerView
    }

    override fun getProgressInterval(): Long {
        return seekProgressInterval
    }

    private fun showOrHidePlayBtn(isShow: Boolean) {
        vPlay?.isSelected = !isShow
        vPlay?.clearAnimation()
        if (isShow) {
            if (vPlay?.visibility == View.VISIBLE) return
            val animIn = AlphaAnimation(0.0f, 1.0f)
            animIn.duration = 300
            vPlay?.startAnimation(animIn)
            vPlay?.visibility = View.VISIBLE
        } else {
            if (vPlay?.visibility == View.GONE) return
            val animIn = AlphaAnimation(1.0f, 0.0f)
            animIn.duration = 300
            vPlay?.startAnimation(animIn)
            vPlay?.visibility = View.GONE
        }
    }

    fun setData(url: String) {
        exoPlayer?.setData(url)
    }

    fun autoPlay(auto: Boolean) {
        vPlay?.clearAnimation()
        vPlay?.visibility = View.GONE
        loadingView?.visibility = View.GONE
        this.autoPlay = auto
    }

    fun setSeekInterval(interval: Long) {
        this.seekProgressInterval = interval
    }

    fun setEventListener(listener: SimpleVideoEventListener) {
        this.simpleVideoEventListener = listener
    }

    fun overrideSeekBar(seekBar: SeekBar?) {
        this.seekBar?.setOnSeekBarChangeListener(null)
        this.seekBar = seekBar
        initSeekBar()
    }

    fun getPath(): String {
        return exoPlayer?.currentPlayPath() ?: ""
    }

    fun onFullScreenPlay(isFull: Boolean) {
        bottomToolsBar?.visibility = if (isFull) VISIBLE else GONE
    }

    fun playOrResume(path: String) {
        if (path == getPath()) {
            exoPlayer?.play()
        } else {
            setData(path)
            exoPlayer?.play()
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun stop() {
        exoPlayer?.stop()
    }

    fun isPause(): Boolean {
        return exoPlayer?.isPause() ?: true
    }

    fun isStop(): Boolean {
        return exoPlayer?.isStop() ?: true
    }

    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying() ?: false
    }

    fun release() {
        seekBar?.isEnabled = false
        videoPlayerView?.player = null
        exoPlayer?.release()
        exoPlayer = null
    }
}