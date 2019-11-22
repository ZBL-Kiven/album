package com.zj.album.ui.folders

import android.view.View

import android.widget.ImageView
import com.zj.album.options.AlbumConfig
import com.zj.album.R
import com.zj.album.imageloader.impl.GlideLoader
import com.zj.album.nHelpers.DataProxy
import com.zj.album.nHelpers.DataStore
import com.zj.album.nModule.FileInfo
import com.zj.album.nModule.FolderInfo
import com.zj.album.ui.base.BaseActivity
import com.zj.album.ui.base.list.adapters.BaseAdapterDataSet
import com.zj.album.ui.base.list.holders.BaseViewHolder
import com.zj.album.ui.base.list.views.EmptyRecyclerView
import com.zj.album.ui.views.BaseLoadingView

/**
 * @author ZJJ on 2019.10.24
 * */
internal class FolderActivity : BaseActivity() {

    private var isStopHandleData: Boolean = false

    private var loadingView: BaseLoadingView? = null

    override fun getContentView(): Int {
        return R.layout.folder_activity
    }

    override fun initView() {
        loadingView = findViewById(R.id.folder_loading)
    }

    override fun initListener() {
        isStopHandleData = false
        loadingView?.setRefreshListener {
            loadingView?.setMode(BaseLoadingView.DisplayMode.LOADING)
            AlbumConfig.loadData()
        }
        findViewById<View>(R.id.folder_cancel).setOnClickListener { finish() }
    }

    override fun onDataDispatch(data: List<FileInfo>?, isQueryTaskRunning: Boolean) {
        when {
            isQueryTaskRunning -> loadingView?.setMode(BaseLoadingView.DisplayMode.LOADING)
            data.isNullOrEmpty() -> loadingView?.setMode(BaseLoadingView.DisplayMode.NO_DATA)
            else -> {
                loadingView?.setMode(BaseLoadingView.DisplayMode.DISMISS)
                if (isStopHandleData) return
                setData()
            }
        }
    }

    private fun setData() {
        val recyclerView = findViewById<EmptyRecyclerView<FolderInfo>>(R.id.folder_lv_file)
        recyclerView.setData(R.layout.folder_item_choose_file, false, DataStore.getFolderData(), object : BaseAdapterDataSet<FolderInfo>() {
            override fun initData(holder: BaseViewHolder, position: Int, data: FolderInfo, payLoads: List<Any>?) {
                holder.getView<View>(R.id.folder_item_select).isSelected = DataStore.isCurDisplayFolder(data.id)
                if (payLoads != null && payLoads.isNotEmpty()) return
                holder.setText(R.id.folder_item_tv_name, data.parentName)
                holder.setText(R.id.folder_item_tv_count, AlbumConfig.getString(R.string.pg_str_picture_count, data.imageCounts))
                val iv = holder.getView<ImageView>(R.id.folder_item_iv_img)
                GlideLoader().loadThumbnail(iv, iv.measuredWidth, R.mipmap.photo_nodata, data.topImgUri ?: "")
            }

            override fun onItemClick(position: Int, v: View, m: FolderInfo?) {
                isStopHandleData = true
                DataProxy.setData(m)
                val adapter = recyclerView.adapter
                adapter?.notifyItemRangeChanged(0, adapter.itemCount, 0)
                finish()
            }
        })
    }
}
