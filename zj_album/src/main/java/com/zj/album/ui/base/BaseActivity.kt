package com.zj.album.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.zj.album.R
import com.zj.album.interfaces.EventHub
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.GraphDataHelper
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.AlbumConfig
import java.util.*

/**
 * @author ZJJ on 2019.10.24
 * */
internal abstract class BaseActivity : AppCompatActivity(), EventHub {

    abstract fun getContentView(): Int
    open fun initView() {}
    open fun initData() {}
    open fun initListener() {}

    private var curDataAccessKey: String = ""
    private var curSelectedAccessKey: String = ""
    private val regKey = UUID.randomUUID().toString()
    final override fun onCreate(savedInstanceState: Bundle?) {
        val i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (i == PackageManager.PERMISSION_GRANTED) {
            start()
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        super.onCreate(savedInstanceState)
    }

    private fun start() {
        setContentView(getContentView())
        initView()
        initData()
        initListener()
    }

    final override fun onDataGot(data: List<FileInfo>?, dataAccessKey: String) {
        this.curDataAccessKey = dataAccessKey
        onDataDispatch(data, GraphDataHelper.isRunning)
    }

    final override fun onSelectedChanged(count: Int, selectedAccessKey: String) {
        this.curSelectedAccessKey = selectedAccessKey
        onSelectedStateChange(count)
    }

    open fun onDataDispatch(data: List<FileInfo>?, isQueryTaskRunning: Boolean) {
    }

    open fun onSelectedStateChange(count: Int) {
    }

    override fun onResume() {
        super.onResume()
        DataProxy.register(regKey, curDataAccessKey, curSelectedAccessKey, this)
    }

    override fun onStop() {
        DataProxy.unregister(regKey)
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (i == PackageManager.PERMISSION_GRANTED) {
            start()
        } else {
            val appName = AlbumConfig.appName
            val name = if (appName.isEmpty()) getString(R.string.app_name) else appName
            Toast.makeText(this, getString(R.string.pg_permission_request_fail, name), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
