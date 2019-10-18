package com.zj.album.base

import android.app.Activity
import android.os.Bundle
import com.zj.album.interfaces.EventHub

internal abstract class BaseActivity : Activity(), EventHub {

    abstract fun initView()
    abstract fun initData()

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    override fun onDataGot() {

    }

    override fun onOriginalCheckedChanged() {

    }

    override fun onSelectedChanged() {

    }



}
