package com.zj.album.ui.photograph

import android.view.View
import android.widget.TextView

import com.zj.album.R

import com.zj.album.nModule.FileInfo
import com.zj.album.ui.base.list.adapters.BaseAdapter
import com.zj.album.ui.base.list.holders.BaseViewHolder

import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import com.zj.album.PhotoAlbum
import com.zj.album.imageloader.impl.GlideLoader
import com.zj.album.nHelpers.DataStore
import com.zj.album.nutils.getDuration

class PhotoGraphAdapter : BaseAdapter<FileInfo>(R.layout.graph_item_selected) {

    override fun initData(holder: BaseViewHolder, position: Int, data: FileInfo, payloads: List<Any>?) {
        val isVideo = data.isVideo
        val simultaneousSelection = PhotoAlbum.simultaneousSelection
        val tvNum = holder.getView<TextView>(R.id.graph_item_tv_num)
        tvNum.setBackgroundResource(if (isVideo && simultaneousSelection) R.drawable.bg_choose_local_video else R.drawable.bg_choose_local_media)
        val isSelected = data.isSelected()
        tvNum.isSelected = isSelected
        val index = DataStore.indexOfSelected(data.path)

        if (!isVideo || simultaneousSelection) {
            tvNum.text = "${if (index >= 0) index + 1 else ""}"
        }
        val flMark = holder.getView<View>(R.id.graph_item_fl_selected)
        if (payloads?.firstOrNull()?.toString() == "$position") {
            flMark.clearAnimation()
            if (isSelected) {
                val animation = AlphaAnimation(0.0f, 1.0f)
                animation.duration = 400
                flMark.startAnimation(animation)
            } else {
                val animation = AlphaAnimation(1.0f, 0.0f)
                animation.duration = 400
                flMark.startAnimation(animation)
            }
        }
        flMark.visibility = if ((!isVideo || simultaneousSelection) && data.isSelected()) VISIBLE else GONE
        if (!payloads.isNullOrEmpty()) {
            return
        }
        val tvDuration = holder.getView<TextView>(R.id.graph_item_tv_duration)
        tvDuration.visibility = if (isVideo) VISIBLE else GONE
        val iv = holder.getView<ImageView>(R.id.graph_item_iv_img)
        GlideLoader().loadThumbnail(iv, iv.measuredWidth / 2, R.drawable.loading_corner_bg, data.path)
        if (isVideo) tvDuration.text = getDuration(data.duration)
        tvNum.setOnClickListener {
            val nextStatus = !data.isSelected()
            data.setSelected(nextStatus).let {
                if (it) {
                    notifyItemRangeChanged(0, itemCount, position)
                }
            }
        }
    }
}
