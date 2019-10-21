package com.zj.album.graphy.adapter;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zj.album.R;
import com.zj.album.entity.SelectionSpec;
import com.zj.album.graphy.module.LocalMedia;
import com.zj.album.graphy.views.IBaseRecyclerAdapter;
import com.zj.album.graphy.views.RecyclerHolder;
import com.zj.album.imageloader.ImageLoader;
import com.zj.album.imageloader.utils.ImageEvaluate;
import com.zj.album.utils.ImageLoaderUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author yangji
 */
public class PreviewAdapter extends IBaseRecyclerAdapter<LocalMedia, PreviewAdapter.ImgHolder> {

    private LayoutInflater mInflater;
    private ViewGroup mParent;

    public PreviewAdapter(OnItemCLickListener listener) {
        super(listener);
    }

    @NonNull
    @Override
    public ImgHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(parent.getContext());
        }
        if (this.mParent == null) {
            this.mParent = parent;
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

            String path = getData().get(i).getFileUri();

            SelectionSpec.INSTANCE.getImageLoader().loadThumbnail(iv, rootView.getLayoutParams().width, 0, path);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemCLickListener.onItemClick(i, view);
                }
            });
        }
    }

}
