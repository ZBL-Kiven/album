package com.zj.album.ui.preview


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.ViewGroup
import com.zj.album.ui.preview.player.VideoView
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

    private var videoView: VideoView? = null

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoView = VideoView(this)
        videoView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setContentView(videoView)
        val url = intent.getStringExtra(PATH)
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        val file = File(url)
        if (!file.exists()) {
            finish()
            return
        }
        videoView?.setData(url)
        videoView?.onFullScreenPlay(true)
    }

    override fun onDestroy() {
        videoView?.onDestroy()
        super.onDestroy()
    }
}