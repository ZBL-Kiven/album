package com.zj.album.graphy.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zj.album.R;
import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.graphy.views.IBaseRecyclerAdapter;
import com.zj.album.graphy.views.RecyclerHolder;
import com.zj.album.utils.ImageLoaderUtils;

import org.jetbrains.annotations.NotNull;

/**
 * Created by zhaojie on 2017/10/26.
 */

public class PreviewAdapter extends IBaseRecyclerAdapter<LocalMedia, PreviewAdapter.ImgHolder> {

    public PreviewAdapter(OnItemCLickListener listener) {
        super(listener);
    }

    @NonNull
    @Override
    public ImgHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_rv, parent, false);
        return new ImgHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgHolder holder, int position) {
        holder.initData(position);
    }

    class ImgHolder extends RecyclerHolder {
        private ImageView iv;

        private ImgHolder(View view) {
            super(view);
            this.iv = view.findViewById(R.id.iem_preview_iv);
        }

        public void initData(final int i) {
            ImageLoaderUtils.load(iv, getData().get(i).getFileUri());
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemCLickListener.onItemClick(i, view);
                }
            });
        }
    }
}
