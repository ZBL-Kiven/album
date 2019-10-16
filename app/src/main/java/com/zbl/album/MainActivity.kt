package com.zbl.album

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zj.album.graphy.activity.PhotoGraphActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAlbum(view: View) {
//        startActivity(Intent(this, PhotoGraphActivity::class.java))

        val intent = Intent(this, PhotoGraphActivity::class.java)
        intent.putExtra("max", 9)
        intent.putExtra("cacheNameCode", "11")
        intent.putExtra("findType", PhotoGraphActivity.IMAGES_AND_VIDEOS)
        startActivityForResult(intent, 12)
    }

    fun shutDown(view: View) {


    }
}
