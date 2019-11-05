package com.zj.album.ui.photograph

import android.view.View
import android.widget.TextView

import com.zj.album.R

import com.zj.album.nModule.FileInfo
import com.zj.album.ui.base.list.adapters.BaseAdapter
import com.zj.album.ui.base.list.holders.BaseViewHolder

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import com.zj.album.nutils.AlbumConfig
import com.zj.album.imageloader.impl.GlideLoader
import com.zj.album.nHelpers.DataStore
import com.zj.album.nutils.Constance
import com.zj.album.nutils.getDuration
import com.zj.album.nutils.showOrHideView

/**
 * @author ZJJ on 2019.10.24
 * */
class PhotoGraphAdapter(private val isOriginalSupport: () -> Boolean) : BaseAdapter<FileInfo>(R.layout.graph_item_selected) {

    override fun initData(holder: BaseViewHolder, position: Int, data: FileInfo, payloads: List<Any>?) {
        val isVideo = data.isVideo
        val simultaneousSelection = AlbumConfig.simultaneousSelection
        val tvNum = holder.getView<TextView>(R.id.graph_item_tv_num)
        val vSelect = holder.getView<View>(R.id.graph_item_btn_num)
        val tvOriginal = holder.getView<TextView>(R.id.graph_item_tv_original)
        val isSelected = data.isSelected()
        tvNum.setBackgroundResource(if (isVideo && !simultaneousSelection) R.drawable.bg_choose_local_video else R.drawable.bg_choose_local_media)
        tvNum.isSelected = isSelected
        tvOriginal.isSelected = data.isOriginal()
        tvOriginal?.visibility = if (isOriginalSupport() && isSelected) VISIBLE else GONE
        val index = DataStore.indexOfSelected(data.path)
        if (!isVideo || simultaneousSelection) {
            tvNum.text = if (index >= 0) "${(index + 1)}" else ""
        }
        val flMark = holder.getView<View>(R.id.graph_item_v_selected)
        if (payloads?.any { it.toString() == "$position" } == true) {
            val floatArray = if (isSelected) floatArrayOf(0.0f, 1.0f) else floatArrayOf(1.0f, 0.0f)
            showOrHideView(flMark, isSelected, floatArray, Constance.ANIMATE_DURATION_2)
        } else {
            flMark.visibility = if (isSelected) VISIBLE else GONE
        }
        if (!payloads.isNullOrEmpty()) {
            return
        }
        val tvDuration = holder.getView<TextView>(R.id.graph_item_tv_duration)
        tvDuration.visibility = if (isVideo) VISIBLE else GONE
        val iv = holder.getView<ImageView>(R.id.graph_item_iv_img)
        GlideLoader().loadThumbnail(iv, iv.measuredWidth / 2, R.drawable.loading_corner_bg, data.path)
        if (isVideo) tvDuration.text = getDuration(data.duration)
        vSelect.setOnClickListener {
            val nextStatus = !data.isSelected()
            if (data.setSelected(nextStatus)) {
                notifyItemRangeChanged(0, itemCount, position)
            }
        }
        tvOriginal?.setOnClickListener {
            val nexState = !data.isOriginal()
            if (data.setOriginal(nexState)) {
                notifyItemRangeChanged(0, itemCount, Constance.NOTIFY_ORIGINAL)
            }
        }
    }
}
