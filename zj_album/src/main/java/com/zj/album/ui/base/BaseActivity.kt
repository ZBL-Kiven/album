package com.zj.album.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zj.album.interfaces.EventHub
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.GraphDataHelper
import com.zj.album.nModule.FileInfo
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
        super.onCreate(savedInstanceState)
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
}
