package com.zj.album.ui.preview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.zj.album.R;
import com.zj.album.imageloader.impl.GlideLoader;
import com.zj.album.nModule.FileInfo;
import com.zj.album.ui.base.list.adapters.BaseAdapter;
import com.zj.album.ui.base.list.holders.BaseViewHolder;

import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @author yangji
 */
public class LandscapeAdapter extends BaseAdapter<FileInfo> {

    private LayoutInflater mInflater;

    public LandscapeAdapter() {
        super(R.layout.preview_photo_selected_item);
    }

    @Override
    protected void initData(BaseViewHolder holder, final int position, FileInfo data, @Nullable List<Object> payloads) {

        ImageView iv = holder.getView(R.id.album_iv_preview);

        new GlideLoader().loadThumbnail(iv, holder.itemView.getLayoutParams().width, 0, data.getPath());

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