package com.zbl.album

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.zj.album.AlbumIns
import com.zj.album.nModule.SimpleSelectInfo
import com.zj.album.nutils.AlbumOptions
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect

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
        AlbumIns.with(this).setOriginalPolymorphism(false).simultaneousSelection(false).maxSelectedCount(9)
            .ignorePaths("QQ").mimeTypes(AlbumOptions.pairOf(AlbumOptions.ofImage(), AlbumOptions.ofVideo()))
            .sortWithDesc(true).useOriginDefault(true)
            .selectedUris(arrayListOf(SimpleSelectInfo("/storage/emulated/0/DCIM/Camera/IMG_20190701_164213.jpg", true)))
            .imageScaleEffect(ScaleEffect.QUAD)
            .pagerTransitionEffect(TransitionEffect.Zoom)
            .start { _, data ->
                Log.e("", "aa  =   ${data?.joinToString { "${it.path} , ${it.useOriginalImages} | " }}")
            }
    }

    fun startAlbum(v: View?) {

        val i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (i != PackageManager.PERMISSION_GRANTED) {
            start()
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }
}
