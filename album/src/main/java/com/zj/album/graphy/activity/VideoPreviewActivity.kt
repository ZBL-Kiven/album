/**
 * Project:  SunPeople-Android
 * Filename: NewMediaPreviewActivity
 *
 * Created by yzf on 2018/5/2.
 * Copyright (c) 2017. cityfruit. All rights reserved.
 */
package com.zj.album.graphy.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.google.android.exoplayer2.ui.PlayerView
import com.zj.album.R
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

    private var iv_play_button: ImageView? = null
    private var iv_videoView: PlayerView? = null


    private val mp = MediaPlayerHelper().apply { start() }
    private lateinit var file: File
    private var play = false
        set(value) {
            field = value
            iv_play_button?.setImageResource(if (value) R.drawable.exo_controls_pause else R.drawable.exo_controls_play)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_preview)
        file = File(intent.getStringExtra(PATH))
        if (!file.exists()) return
        mp.ready(
            iv_videoView!!,
            Uri.fromFile(file),
            object : MediaPlayerHelper.VideoPlayEventListener {
                override fun onProgress(currentProgress: Int, maxProgress: Int) {
                }

                override fun onStatusChange(status: MediaPlayerHelper.Status) {
                    when (status) {
                        MediaPlayerHelper.Status.Start, MediaPlayerHelper.Status.Resume, MediaPlayerHelper.Status.SeekTo -> this@VideoPreviewActivity.play =
                            true
                        MediaPlayerHelper.Status.End, MediaPlayerHelper.Status.Pause -> this@VideoPreviewActivity.play =
                            false
                    }
                }
            })
        initView()
        initListener()
    }

    private fun initView() {
        iv_play_button = findViewById(R.id.iv_play_button)
        iv_videoView = findViewById(R.id.iv_videoView)

        iv_videoView?.useController = false
    }

    private fun initListener() {
        findViewById<View>(R.id.iv_media_cancel).setOnClickListener { finish() }
        findViewById<View>(R.id.media_send).setOnClickListener { onSendClick }
        findViewById<View>(R.id.iv_play_button).setOnClickListener { onPlayClick }
    }

    private val onPlayClick = View.OnClickListener {
        when (play) {
//            null -> mp.start()
            true -> mp.pause()
            false -> mp.resume()
        }
    }

    private val onSendClick = View.OnClickListener {
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
        mp.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.release()
    }
}