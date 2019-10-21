package com.zbl.album

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zj.album.PhotoAlbum

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PhotoAlbum.startPhotoGraphActivity(this, 0, 9, 0)
    }
}
