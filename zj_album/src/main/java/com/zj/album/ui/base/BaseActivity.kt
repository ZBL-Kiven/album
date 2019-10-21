package com.zj.album.ui.base

import android.os.Bundle
import android.support.annotation.CallSuper
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

    private var curAccessKey: String = ""
    private val regKey = UUID.randomUUID().toString()
    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        initView()
        initData()
        initListener()
    }

    @CallSuper
    override fun onDataGot(data: List<FileInfo>?, curAccessKey: String) {
        this.curAccessKey = curAccessKey
    }

    @CallSuper
    override fun onOriginalCheckedChanged(useOriginal: Boolean, curAccessKey: String) {
        this.curAccessKey = curAccessKey
    }

    override fun onSelectedChanged(count: Int) {

    }

    override fun onResume() {
        super.onResume()
        DataProxy.register(regKey, curAccessKey, this)
    }

    override fun onStop() {
        DataProxy.unregister(regKey)
        super.onStop()
    }
}
