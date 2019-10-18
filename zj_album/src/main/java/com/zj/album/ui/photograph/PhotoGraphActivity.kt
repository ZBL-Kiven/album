package com.zj.album.ui.photograph

import com.zj.album.base.BaseActivity
import com.zj.album.nutils.Constance
import com.zj.album.nutils.getValueBySafe

internal class PhotoGraphActivity : BaseActivity() {

    /**activity start code*/
    private var requestCode: Int = -1
    /**the max  selected size*/
    private var maxSelectSize: Int = Int.MAX_VALUE
    /**is use original data default*/
    private var useOriginDefault: Boolean = false


    override fun initView() {

    }

    override fun initData() {
        getBundleData()
    }

    private fun getBundleData() {
        val bundle = intent.extras ?: return
        try {
            maxSelectSize = bundle.getValueBySafe(Constance.MAX_SELECT_COUNT, 0)
            requestCode = bundle.getValueBySafe(Constance.REQUEST_CODE, -1)
            useOriginDefault = bundle.getValueBySafe(Constance.USE_ORIGINAL_DEFAULT, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
