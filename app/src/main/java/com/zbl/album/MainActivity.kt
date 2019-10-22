package com.zbl.album

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zj.album.PhotoAlbum
import com.zj.album.ui.preview.VideoPreviewActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun startAlbum(view: View) {
        PhotoAlbum.startPhotoGraphActivity(this, 0, 9, 0)
    }

    fun shutDown(view: View) {

    }

    fun startPreview(view: View) {
        VideoPreviewActivity.start(this, "/sdcard/DCIM/Camera/20191021_185822.mp4", 200)
    }
}
