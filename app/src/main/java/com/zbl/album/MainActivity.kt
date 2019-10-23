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
        PhotoAlbum.options(this,0).maxSelectedCount(9).ignorePaths("Camera").start()
    }
}
