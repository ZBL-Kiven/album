@file:Suppress("unused")

package com.zj.album.ui.preview

import android.os.Build
import android.view.View
import android.widget.ImageView
import com.zj.album.R
import com.zj.album.imageloader.impl.GlideLoader
import com.zj.album.nModule.FileInfo
import com.zj.album.ui.base.list.adapters.BaseAdapter
import com.zj.album.ui.base.list.holders.BaseViewHolder
import com.zj.album.ui.views.SquareImageView
import kotlin.math.max

internal class PreviewSelectedAdapter : BaseAdapter<FileInfo>(R.layout.preview_photo_selected_item) {

    override fun initData(holder: BaseViewHolder?, position: Int, data: FileInfo?, payloads: MutableList<Any>?) {
        data?.let {
            val iv = holder?.getView<SquareImageView>(R.id.preview_selected_iv)
            val tag = holder?.getView<ImageView>(R.id.preview_selected_video_tag)
            tag?.visibility = if (it.isVideo) View.VISIBLE else View.GONE
            iv?.let { imageView ->
                val path = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) data.path else data.getContentUri()
                GlideLoader().loadThumbnail(iv, imageView.measuredWidth / 2, 0, path)
            }
        }
    }

    fun getDataPosition(info: FileInfo?): Int {
        val index = -1
        info?.let {
            data?.indexOfFirst { it.path == info.path }
        }
        return max(index, 0)
    }
}
