package com.zj.album.ui.preview.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.zj.album.R
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.Constance
import com.zj.album.nutils.getDuration
import com.zj.album.nutils.showOrHideView
import com.zj.album.options.AlbumConfig.getString
import com.zj.album.ui.views.BaseLoadingView
import com.zj.album.ui.views.image.TouchScaleImageView
import com.zj.player.base.InflateInfo
import com.zj.player.ut.Controller
import com.zj.player.ut.PlayQualityLevel
import com.zj.player.z.ZController
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * @author ZJJ on 2019.10.24
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class VideoController : Controller, Handler.Callback {

    companion object {
        private var handler: Handler? = null
        private var player: Player? = null
        private var curPlayerView: WeakReference<VideoController>? = null

        fun setData(target: ViewGroup, seekBar: SeekBar?, start: TextView?, end: TextView?, data: FileInfo?) {
            if (data == null) return
            if (curPlayerView?.get() == null) {
                curPlayerView = WeakReference(VideoController())
            }
            curPlayerView?.get()?.let {
                it.target = target
                it.seekBar = seekBar
                it.start = start
                it.end = end
                it.data = data
                it.vPlay = target.findViewById(R.id.preview_base_btn_video_play)
                it.ivBg = target.findViewById(R.id.preview_base_iv_img)
                it.blv = target.findViewById(R.id.video_preview_loading)
                it.initView()
                player?.onFocus(true)
            }
        }

        fun release() {
            player?.release()
            player = null
            handler?.removeCallbacksAndMessages(null)
            handler = null
        }

        fun stop() {
            player?.stop()
        }

        fun onFocus(hasFocus: Boolean) {
            player?.onFocus(hasFocus)
            if (!hasFocus) {
                player?.updateViewController("--", null)
            }
        }

        fun pause() {
            player?.pause()
        }

        fun full(isFull: Boolean): Boolean {
            return player?.full(isFull) ?: true
        }

        fun isPlaying(): Boolean {
            return player?.isPlaying() == true
        }
    }

    private var isTickingSeekBarFromUser = false
    private var videoTotalTime = 0L
    private var isBindController = false
    private var hasFocus = false
    private lateinit var target: ViewGroup
    private var seekBar: SeekBar? = null
    private var start: TextView? = null
    private var end: TextView? = null
    private var data: FileInfo? = null
    private var vPlay: View? = null
    private var ivBg: TouchScaleImageView? = null
    private var blv: BaseLoadingView? = null
    private var curPathIsHttp: Boolean = false

    private fun initView() {
        seekBar?.isEnabled = false
        start?.text = getString(R.string.pv_str_default_time)
        end?.text = getDuration(data?.duration ?: 0)
        curPathIsHttp = data?.path?.startsWith("http") == true
        vPlay?.setOnClickListener {
            if (isPlaying()) {
                player?.pause()
            } else {
                val path = Uri.parse(data?.path ?: "").toString()
                if (isBindController) player?.playOrResume(path)
                else play(path)
            }
        }
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
                val seekProgress = (max(0f, min(100, seekBar?.progress ?: 0) / 100f * max(videoTotalTime, 1) - 1)).toLong()
                if (isTickingSeekBarFromUser && p2) {
                    player?.seekTo(seekProgress, true)
                    handler?.removeMessages(102021)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isTickingSeekBarFromUser = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isTickingSeekBarFromUser = false
                player?.autoPlayWhenReady(true)
                handler?.sendEmptyMessageDelayed(102021, 3000.toLong())
            }
        })
    }

    fun play(path: String?) {
        if (player == null) {
            player = Player(this::class.java.simpleName, this)
        } else {
            player?.updateViewController(this::class.java.simpleName, this)
        }
        vPlay?.isSelected = true
        player?.playOrResume(path ?: "")
        if (handler == null) handler = Handler(Looper.getMainLooper(), this)
        showOrHidePlayBtn(false)
    }

    fun full(isFull: Boolean): Boolean {
        if (player?.isLoadData() != true) return true
        val inFullAndPlayBtnDismissed = !isFull && vPlay?.visibility != View.VISIBLE
        if (inFullAndPlayBtnDismissed) {
            if (player?.isLoading(true) == false && player?.isLoadData() == true && inFullAndPlayBtnDismissed) {
                showOrHidePlayBtn(true)
            }
        } else {
            if ((isFull && vPlay?.visibility != View.VISIBLE) || (!isFull && vPlay?.visibility == View.VISIBLE && isPlaying())) {
                showOrHidePlayBtn(isFull)
            }
        }
        handler?.removeMessages(102021)
        handler?.sendEmptyMessageDelayed(102021, 3000)
        return !inFullAndPlayBtnDismissed
    }

    fun onFocus(hasFocus: Boolean) {
        this.hasFocus = hasFocus
    }

    override fun onPlay(path: String?, p1: Boolean) {
        blv?.setMode(BaseLoadingView.DisplayMode.NONE)
        ivBg?.visibility = View.GONE
        seekBar?.isEnabled = true
        seekBar?.isSelected = true
        vPlay?.isSelected = true
        if (vPlay?.visibility != View.GONE) vPlay?.visibility = View.GONE
    }

    override fun onPause(path: String, isRegulate: Boolean) {
        vPlay?.isSelected = false
        if (vPlay?.visibility != View.VISIBLE) showOrHidePlayBtn(true)
        seekBar?.isSelected = false
        blv?.setMode(BaseLoadingView.DisplayMode.NONE)
    }

    override fun onStop(path: String, isRegulate: Boolean) {
        ivBg?.visibility = View.VISIBLE
        seekBar?.progress = 0
        vPlay?.isSelected = false
        seekBar?.isEnabled = false
        seekBar?.isSelected = false
        if (vPlay?.visibility != View.VISIBLE) showOrHidePlayBtn(true)
        blv?.setMode(BaseLoadingView.DisplayMode.NONE, "", true)
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onLoading(path: String?, isRegulate: Boolean) {
        vPlay?.visibility = View.GONE
        vPlay?.isSelected = false
        if (hasFocus && player?.isLoadData() == true) blv?.setMode(BaseLoadingView.DisplayMode.LOADING)
    }

    override fun onSeekingLoading(path: String?) {
        vPlay?.isSelected = false
        vPlay?.visibility = View.GONE
        if (hasFocus && player?.isLoadData() == true) blv?.setMode(BaseLoadingView.DisplayMode.LOADING)
    }

    override fun onControllerBind(controller: ZController<*, *>?) {
        Log.e("------ ", "onControllerBind =  $controller")
        this.isBindController = controller != null
    }

    override fun getContext(): Context {
        return target.context
    }

    override fun keepScreenOnWhenPlaying(): Boolean {
        return true
    }

    override fun getControllerInfo(): InflateInfo {
        return InflateInfo(target, 2)
    }

    override fun onPrepare(path: String, videoSize: Long, isRegulate: Boolean) {
        videoTotalTime = videoSize
        end?.text = getDuration(videoSize)
    }

    override fun onSeekChanged(seek: Int, buffered: Long, fromUser: Boolean, played: Long, videoSize: Long) {
        if (isTickingSeekBarFromUser) return
        this.videoTotalTime = videoSize
        if (!fromUser) {
            seekBar?.progress = seek
            if (curPathIsHttp) {
                val bf = (buffered * 1.0f / max(1, videoSize) * 100 + 0.5f).toInt()
                seekBar?.secondaryProgress = bf
            }
            val startProgress = videoSize / 100f * seek
            start?.text = getDuration(startProgress.toLong())
        }
    }

    override fun onError(e: Exception?) {
        if (!hasFocus || player?.isLoadData() != true) return
        if (curPathIsHttp) blv?.setMode(BaseLoadingView.DisplayMode.NET_ERROR)
        else blv?.setMode(BaseLoadingView.DisplayMode.NO_DATA)
    }

    override fun updateCurPlayerInfo(volume: Int, speed: Float) {}
    override fun updateCurPlayingQuality(p0: PlayQualityLevel?, p1: MutableList<PlayQualityLevel>?) {}
    override fun onDestroy(path: String?, isRegulate: Boolean) {}
    override fun completing(path: String?, isRegulate: Boolean) {}
    override fun onCompleted(path: String?, isRegulate: Boolean) {}

    private fun showOrHidePlayBtn(isShow: Boolean) {
        val start = if (isShow) 0.0f else 1.0f
        val end = if (isShow) 1.0f else 0.0f
        showOrHideView(vPlay, isShow, floatArrayOf(start, end), Constance.ANIMATE_DURATION)
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 102021) {
            showOrHidePlayBtn(false)
        }
        return false
    }
}