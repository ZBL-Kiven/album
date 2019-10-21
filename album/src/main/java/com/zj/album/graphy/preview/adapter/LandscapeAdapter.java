package com.zj.album.graphy.preview.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zj.album.R;
import com.zj.album.graphy.views.IBaseRecyclerAdapter;
import com.zj.album.graphy.views.RecyclerHolder;
import com.zj.album.imageloader.impl.GlideLoader;

import org.jetbrains.annotations.NotNull;

public class LandscapeAdapter extends IBaseRecyclerAdapter<String, LandscapeAdapter.ImgHolder> {

    private LayoutInflater mInflater;

    public LandscapeAdapter(OnItemCLickListener listener) {
        super(listener);
    }

    @NonNull
    @Override
    public ImgHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(parent.getContext());
        }

        View v = mInflater.inflate(R.layout.item_preview_rv, parent, false);
        return new ImgHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgHolder holder, int position) {
        holder.initData(position);
    }

    class ImgHolder extends RecyclerHolder {
        private ImageView iv;
        private View rootView;

        private ImgHolder(View view) {
            super(view);
            rootView = view;
            this.iv = view.findViewById(R.id.iem_preview_iv);
        }

        void initData(final int i) {

            String path = getData().get(i);

            new GlideLoader().loadThumbnail(iv, rootView.getLayoutParams().width, 0, path);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemCLickListener.onItemClick(i, view);
                }
            });
        }
    }

}