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

@Suppress("unused")
class VideoView : FrameLayout, PlayerEvent {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        init()
        initListener()
    }

    private var vPlay: View? = null
    private var flPlay: View? = null
    private var tvStart: TextView? = null
    private var tvEnd: TextView? = null
    private var seekBar: SeekBar? = null
    private var loadingView: BaseLoadingView? = null
    private var bottomToolsBar: View? = null
    private var videoPlayerView: PlayerView? = null
    private var isTickingSeekBarFromUser: Boolean = false
    private var autoPlay = false

    private var exoPlayer: ExoPlayer? = null

    private fun init() {
        val root = View.inflate(context, R.layout.preview_video, this)
        videoPlayerView = root.findViewById(R.id.video_preview_PlayerView)
        vPlay = root.findViewById(R.id.video_preview_iv_play)
        flPlay = root.findViewById(R.id.video_preview_fl_play)
        tvStart = root.findViewById(R.id.video_preview_tv_start)
        tvEnd = root.findViewById(R.id.video_preview_tv_end)
        loadingView = root.findViewById(R.id.video_preview_loading)
        bottomToolsBar = root.findViewById(R.id.video_preview__tools_bar)
        seekBar = root.findViewById(R.id.video_preview_sb)
        videoPlayerView?.useController = false
        exoPlayer = ExoPlayer(this)
    }

    fun setData(url: String) {
        exoPlayer?.initData(url)
        onSeekChanged(0, false)
    }

    fun autoPlay(auto: Boolean) {
        vPlay?.clearAnimation()
        vPlay?.visibility = View.GONE
        loadingView?.visibility = View.GONE
        this.autoPlay = auto
    }

    private fun initListener() {
        flPlay?.setOnClickListener {
            it.isEnabled = false
            if (!it.isSelected) {
                exoPlayer?.resume()
            } else {
                exoPlayer?.pause()
            }
            it.isEnabled = true
        }

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
                exoPlayer?.resume()
            }
        })
    }

    override fun onLoading(path: String) {
        if (!autoPlay) loadingView?.setMode(BaseLoadingView.DisplayMode.loading)
        seekBar?.isEnabled = false
    }

    fun onFullScreenPlay(isFull: Boolean) {
        bottomToolsBar?.visibility = if (isFull) VISIBLE else GONE
    }

    override fun onPrepare(path: String) {
        if (!autoPlay) loadingView?.setMode(BaseLoadingView.DisplayMode.normal)
        seekBar?.isEnabled = true
        tvEnd?.text = getDuration(exoPlayer?.getDuration() ?: 0)
        if (autoPlay) exoPlayer?.resume()
    }

    override fun onPlay(path: String) {
        flPlay?.isSelected = true
        seekBar?.isSelected = true
        isTickingSeekBarFromUser = false
        showOrHidePlayBtn(false)
    }

    override fun onPause(path: String) {
        flPlay?.isSelected = false
        seekBar?.isSelected = false
        showOrHidePlayBtn(true)
    }

    override fun onCompleted(path: String) {
        flPlay?.isSelected = false
        seekBar?.isSelected = false
        showOrHidePlayBtn(true)
        onSeekChanged(0, false)
    }

    override fun onSeekChanged(seek: Int, fromUser: Boolean) {
        if (!isTickingSeekBarFromUser && !fromUser) {
            seekBar?.progress = seek
        }
        val curDuration = exoPlayer?.getDuration() ?: 0
        val startProgress = curDuration / 100f * seek
        tvStart?.text = getDuration(startProgress.toLong())
    }

    override fun onError(e: Exception?) {
        loadingView?.setMode(BaseLoadingView.DisplayMode.noData)
    }

    override fun getPlayerView(): PlayerView? {
        return videoPlayerView
    }

    override fun getProgressPrevios(): Long {
        return 16
    }

    private fun showOrHidePlayBtn(isShow: Boolean) {
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

    fun pause() {
        exoPlayer?.pause()
    }

    fun isStop(): Boolean {
        return exoPlayer?.isStop() ?: true
    }

    fun isResume(): Boolean {
        return exoPlayer?.isResumed() ?: false
    }


    fun release() {
        exoPlayer?.release()
        videoPlayerView?.player = null
        flPlay?.isSelected = false
        seekBar?.isEnabled = false
    }
}