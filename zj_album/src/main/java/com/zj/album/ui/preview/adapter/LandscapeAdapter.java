package com.zj.album.ui.preview.adapter;

import android.view.LayoutInflater;
import android.view.View;

import com.zj.album.R;
import com.zj.album.imageloader.impl.GlideLoader;
import com.zj.album.nModule.FileInfo;
import com.zj.album.ui.base.list.adapters.BaseAdapter;
import com.zj.album.ui.base.list.holders.BaseViewHolder;
import com.zj.album.widget.image.ImageViewTouch;


/**
 * @author yangji
 */
public class LandscapeAdapter extends BaseAdapter<FileInfo> {

    private LayoutInflater mInflater;

    public LandscapeAdapter() {
        super(R.layout.album_item_preview);
    }

    @Override
    protected void initData(BaseViewHolder holder, final int position, FileInfo info) {

        ImageViewTouch iv = holder.getView(R.id.album_iv_preview);

        new GlideLoader().loadThumbnail(iv, holder.itemView.getLayoutParams().width, 0, info.getPath());

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onItemClick(position, view, getItem(position));
                }
            }
        });
    }
}