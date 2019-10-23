package com.zj.album.ui.preview


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.google.android.exoplayer2.ui.PlayerView
import com.zj.album.R
import com.zj.album.ui.preview.helper.MediaPlayerHelper
import java.io.File

//拍照或者视频预览页
class VideoPreviewActivity : AppCompatActivity() {

    companion object {
        private const val PATH = "path"

        @JvmStatic
        fun start(context: Activity, path: String, requestCode: Int) {
            val intent = Intent(context, VideoPreviewActivity::class.java)
            intent.putExtra(PATH, path)
            context.startActivityForResult(intent, requestCode)
        }
    }

    private var ivPlayButton: ImageView? = null
    private var playerView: PlayerView? = null
    private var clVideoBottomBar: View? = null
    private var mediaPlayer: MediaPlayerHelper? = null

    private var mIsFullPreview = false

    val listener = object : MediaPlayerHelper.VideoPlayEventListener {
        override fun onProgress(currentProgress: Int, maxProgress: Int) {
        }

        override fun onStatusChange(status: MediaPlayerHelper.Status) {
            when (status) {
                MediaPlayerHelper.Status.Start, MediaPlayerHelper.Status.Resume, MediaPlayerHelper.Status.SeekTo ->
                    this@VideoPreviewActivity.play = true
                MediaPlayerHelper.Status.End ->
                    this@VideoPreviewActivity.play = false
                MediaPlayerHelper.Status.Pause -> {
                    this@VideoPreviewActivity.play = false
                }

            }
        }
    }

    private lateinit var file: File
    private var play = false
        set(value) {
            field = value
            ivPlayButton?.setImageResource(if (value) R.drawable.exo_controls_pause else R.drawable.exo_controls_play)
        }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(PATH);
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        file = File(url)
        if (!file.exists()) {
            finish()
            return
        }

        setContentView(R.layout.activity_video_preview)

        initView()

        initListener()

        initData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        clVideoBottomBar = findViewById(R.id.cl_video_bottom_bar)
        ivPlayButton = findViewById(R.id.iv_play_button)
        playerView = findViewById(R.id.iv_videoView)
        playerView?.useController = false
        playerView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                full()
            }
            true
        }
    }

    private fun initListener() {
        findViewById<View>(R.id.iv_media_cancel).setOnClickListener { finish() }
        findViewById<View>(R.id.tv_media_send).setOnClickListener { sendMedia() }
        findViewById<View>(R.id.iv_play_button).setOnClickListener { onPlayOrPause() }
    }

    private fun initData() {
        mediaPlayer = MediaPlayerHelper(applicationContext).apply { start() }
        mediaPlayer?.ready(
            playerView!!,
            Uri.fromFile(file), listener
        )
    }

    private fun onPlayOrPause() {
        when (play) {
            true -> mediaPlayer?.pause()
            false -> mediaPlayer?.resume()
        }
    }

    private fun sendMedia() {
        val intent = Intent()

        // todo 文件大小配置
//        val videoDuration = getVideoDuration(file.path)
//        val fileSize = (file.length() / 1024 / 1024).toInt()
//        if (fileSize > 500 || videoDuration > 60 * 5) {
//            getSimpleDialog(this, getString(R.string.im_file_exceeds, fileSize, videoDuration),
//                    getString(R.string.im_file_is_too_large)).setPositiveButton(R.string.im_ok, null).show()
//            return@OnClickListener
//        }
        intent.putExtra("uri", file.path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun getVideoDuration(filePath: String): Int {
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(filePath)
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val result = Integer.parseInt(if (TextUtils.isEmpty(duration)) "0" else duration)
            return result / 1000
        } catch (e: RuntimeException) {
            return 0
        }
    }

    private fun getSimpleDialog(
        context: Context,
        message: String?,
        title: String?
    ): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        if (!title.isNullOrEmpty()) {
            builder.setTitle(title)
        }
        builder.setMessage(message)
        return builder
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer?.pause()
    }

    private fun full() {
        if (mIsFullPreview) {
            clVideoBottomBar.let {
                it!!.animate()
                    .translationYBy((-it.measuredHeight).toFloat())
                    .setInterpolator(FastOutSlowInInterpolator())
                    .setDuration(200)
                    .start()
            }
        } else {
            clVideoBottomBar.let {
                it!!.animate()
                    .translationYBy((it.measuredHeight).toFloat())
                    .setInterpolator(FastOutSlowInInterpolator())
                    .setDuration(200)
                    .start()
            }

        }
        mIsFullPreview = !mIsFullPreview
    }
}