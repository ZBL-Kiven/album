package com.zj.album.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zj.album.interfaces.EventHub
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nModule.FileInfo
import java.util.*

internal abstract class BaseActivity : AppCompatActivity(), EventHub {

    abstract fun getContentView(): Int
    open fun initView() {}
    open fun initData() {}
    open fun initListener() {}

    private val regKey = UUID.randomUUID().toString()
    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        initView()
        initData()
        initListener()
    }

    override fun onDataGot(data: List<FileInfo>?) {}

    override fun onOriginalCheckedChanged(useOriginal: Boolean) {}

    override fun onSelectedChanged() {}

    override fun onResume() {
        super.onResume()
        DataProxy.register(regKey, this)
    }

    override fun onStop() {
        DataProxy.unregister(regKey)
        super.onStop()
    }
}
