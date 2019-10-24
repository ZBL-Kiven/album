package com.zj.album.ui.previews

import android.app.Activity
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.zj.album.R
import com.zj.album.nHelpers.DataStore
import com.zj.album.nModule.FileInfo
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.base.list.listeners.ItemClickListener
import com.zj.album.ui.preview.ZoomPageTransformer
import com.zj.album.ui.preview.adapter.LandscapeAdapter
import com.zj.album.ui.preview.adapter.PreviewImageAdapter
import com.zj.album.ui.preview.listener.FullPreviewListener
import com.zj.album.widget.JViewPager

internal class PreviewImageActivity : BaseActivity(), FullPreviewListener {

    private var viewPager: JViewPager? = null
    private var rlPreviewTop: View? = null
    private var rlPreviewBottom: View? = null
    private var tvSelectCount: TextView? = null

    private var dlPreviewFlSelected: View? = null
    private var dlPreviewTvSelected: TextView? = null

    private var mIsFullPreview = false

    private var previewAdapter: PreviewImageAdapter? = null
    private var mLandscapeAdapter: LandscapeAdapter? = null

    private val itemClickListener = object : ItemClickListener<FileInfo>() {
        override fun onItemClick(position: Int, v: View, m: FileInfo?) {
            indexOf(mLandscapeAdapter?.getItem(position))
        }
    }

    override fun getContentView(): Int {
        return R.layout.preview_activity
    }

    override fun initView() {
        rlPreviewTop = findViewById(R.id.rl_preview_top)
        dlPreviewFlSelected = findViewById(R.id.dl_preview_fl_selected)
        dlPreviewTvSelected = findViewById(R.id.dl_preview_tv_selected)

        rlPreviewBottom = findViewById(R.id.rl_preview_bottom)

        tvSelectCount = findViewById(R.id.tv_select_count)


        viewPager = findViewById(R.id.dl_preview_viewpager)
        viewPager?.setLooper(true)
        viewPager?.setPageTransformer(true, ZoomPageTransformer())

        val rvSelectImg = findViewById<RecyclerView>(R.id.rv_select_img)
        //设置横向预览
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        rvSelectImg.layoutManager = linearLayoutManager
        rvSelectImg.overScrollMode = View.OVER_SCROLL_NEVER
        mLandscapeAdapter = LandscapeAdapter()
        mLandscapeAdapter?.setOnItemClickListener(itemClickListener)
        rvSelectImg.adapter = mLandscapeAdapter
    }


    override fun initListener() {
        viewPager?.addOnPageChangeListener(object : JViewPager.PageChangeListener() {
            override fun onPageSelected(currentItem: Int) {
                previewAdapter?.reset(currentItem)
                updateTopSelected(previewAdapter?.getItem(currentItem))
            }
        })

        dlPreviewFlSelected?.setOnClickListener {
            //设置选中
            val currentItem = viewPager?.currentItem ?: return@setOnClickListener
            val info = previewAdapter?.getItem(currentItem)
            info?.setSelected(!info.isSelected(), false)
            updateTopSelected(info)
            updateLandscapeAdapter()
        }

        findViewById<View>(R.id.rv_select_img).setOnClickListener { cancel() }

        findViewById<View>(R.id.photo_send).setOnClickListener { commit() }
    }


    override fun initData() {
        updateLandscapeAdapter()
    }

    /**
     * 全预览
     */
    private fun indexOf(info: FileInfo?) {
        setCurrentItem(previewAdapter?.getItemPosition(info))
    }

    /**
     * 更新当前图片是否选中，选中下标
     */
    private fun updateTopSelected(info: FileInfo?) {
        if (info?.isSelected() == true) {
            dlPreviewTvSelected?.isSelected = true
            dlPreviewTvSelected?.text = (1 + DataStore.indexOfSelected(info.path)).toString()
        } else {
            dlPreviewTvSelected?.isSelected = false
        }
    }

    /**
     * 跳转到选中位置
     *
     * @param index 选中的下标
     */
    private fun setCurrentItem(index: Int?) {
        var i = index ?: return
        if (i == -1) {
            i = 0
        }
        viewPager?.setCurrentItem(i, false)
    }

    /**
     * 更新当前选中数量
     */
    override fun onSelectedStateChange(count: Int) {
        if (count == 0) {
            tvSelectCount?.visibility = View.GONE
        } else {
            tvSelectCount?.visibility = View.VISIBLE
            tvSelectCount?.text = "$count"
        }
    }

    /**
     * 更新横向选中预览
     */
    private fun updateLandscapeAdapter() {
        mLandscapeAdapter?.clear()
        mLandscapeAdapter?.add(DataStore.getCurSelectedData())
        mLandscapeAdapter?.notifyDataSetChanged()
    }

    override fun onDataDispatch(data: List<FileInfo>?, isQueryTaskRunning: Boolean) {
        previewAdapter = PreviewImageAdapter(true, this)
        previewAdapter?.setItems(data)
        viewPager?.adapter = previewAdapter
        previewAdapter?.notifyDataSetChanged()
    }

    /**
     * full screen
     */
    override fun onFull() {
        if (mIsFullPreview) {
            rlPreviewTop?.animate()?.setInterpolator(FastOutSlowInInterpolator())?.translationYBy(((rlPreviewTop?.measuredHeight ?: 0).toFloat()))?.setDuration(200)?.start()
            rlPreviewBottom?.animate()?.translationYBy((-(rlPreviewBottom?.measuredHeight ?: 0).toFloat()))?.setInterpolator(FastOutSlowInInterpolator())?.setDuration(200)?.start()
        } else {
            rlPreviewTop?.animate()?.setInterpolator(FastOutSlowInInterpolator())?.translationYBy((-(rlPreviewTop?.measuredHeight ?: 0)).toFloat())?.setDuration(200)?.start()
            rlPreviewBottom?.animate()?.setInterpolator(FastOutSlowInInterpolator())?.translationYBy(((rlPreviewBottom?.measuredHeight ?: 0)).toFloat())?.setDuration(200)?.start()
        }
        mIsFullPreview = !mIsFullPreview
    }

    /**
     * 取消
     */
    private fun cancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /**
     * 提交
     */
    private fun commit() {
        setResult(Activity.RESULT_OK)
        finish()
    }

}
