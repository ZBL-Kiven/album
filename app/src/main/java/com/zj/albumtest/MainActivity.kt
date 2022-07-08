package com.zj.albumtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.zj.album.AlbumIns
import com.zj.album.nutils.MimeType
import com.zj.album.options.AlbumOptions
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        start()
    }

    private fun start() {
        AlbumIns.with(this)
            .setOriginalPolymorphism(true)
            .simultaneousSelection(true)
            .maxSelectedCount(9)

            .mutableTypeSize()
            .addNewRule("Image",3,AlbumOptions.ofStaticImage())
            .addNewRule("Gif", 1, EnumSet.of(MimeType.GIF))
            .addNewRule("Video",2,AlbumOptions.ofVideo())
            .set()

            .mimeTypes(AlbumOptions.pairOf(AlbumOptions.ofImage(), AlbumOptions.ofVideo()))
            .sortWithDesc(true)
            .useOriginDefault(false)
            .imgSizeRange(1, 20000000)
            .videoSizeRange(1, 200000000)
            .imageScaleEffect(ScaleEffect.QUAD)
            .pagerTransitionEffect(TransitionEffect.Zoom)
            .start { _, data ->
                findViewById<TextView>(R.id.main_tv)?.text = data?.joinToString {
                    "\n path = ${it.path} \n" + " original = ${it.useOriginalImages} | \n"
                }
            }
    }

    fun startAlbum(@Suppress("UNUSED_PARAMETER") v: View?) {
        val i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (i != PackageManager.PERMISSION_GRANTED) {
            start()
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }
}
