package com.zbl.album

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.zbl.album.omnitpotent.ActivityResult
import com.zbl.album.omnitpotent.interfaces.ResultListener
import com.zj.album.graphy.preview.PreviewImageActivity
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
        val bundle = Bundle();
        bundle.putInt("max", 9)
        bundle.putInt("findType", PhotoGraphActivity.IMAGES_AND_VIDEOS)
        bundle.putString("cacheNameCode", "11")

        ActivityResult.with(this)
            .activity(PhotoGraphActivity::class.java)
            .bundle(bundle)
            .build(object : AlbumListener() {
                override fun success(fullSize: Boolean) {
                    Toast.makeText(this@MainActivity, "成功了$fullSize", Toast.LENGTH_SHORT).show();
                }

                override fun cancel() {
                    Toast.makeText(this@MainActivity, "失败了", Toast.LENGTH_SHORT).show();
                }
            })


    }

    fun startPreview(view: View) {
        ActivityResult.with(this)
            .activity(PreviewImageActivity::class.java)
            .build(object : ResultListener {
                override fun cancel() {

                }

                override fun success(data: Intent?) {

                }
            })
    }

}
