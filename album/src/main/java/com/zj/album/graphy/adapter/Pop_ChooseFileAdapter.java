package com.zj.album.graphy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.album.R;
import com.zj.album.entity.SelectionSpec;
import com.zj.album.graphy.module.PhotoFileInfo;
import com.zj.album.graphy.views.IBaseAdapter;
import com.zj.album.graphy.views.IRecyclerAdapter;

public class Pop_ChooseFileAdapter extends IBaseAdapter<PhotoFileInfo> {

    private IRecyclerAdapter.OnItemCLickListener clickListener;
    private LayoutInflater mInflater;
    private ViewGroup mViewGroup;
    private int mImageSize = 0;

    public Pop_ChooseFileAdapter(IRecyclerAdapter.OnItemCLickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(viewGroup.getContext());
            this.mViewGroup = viewGroup;
        }

        FileHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_pop_choosefile, viewGroup, false);
            holder = new FileHolder(view);
            view.setTag(holder);
        } else {
            holder = (FileHolder) view.getTag();
        }
        holder.initData(i, view);
        return view;
    }

    private class FileHolder {
        private TextView tvName;
        private ImageView ivImg;
        private TextView tvCount;

        private FileHolder(View view) {
            ivImg = view.findViewById(R.id.item_cp_ivImg);
            tvName = view.findViewById(R.id.item_cp_tvName);
            tvCount = view.findViewById(R.id.tv_count);
        }

        private void initData(final int i, final View v) {
            final PhotoFileInfo info = getInfos().get(i);

            SelectionSpec.INSTANCE.getImageLoader().loadThumbnail(ivImg, getImageSize(), 0, info.topImgUri);

            tvName.setText(info.parentPath);
            tvCount.setText(String.valueOf(info.imageCounts));
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(i, v);
                }
            });
        }
    }

    private int getImageSize() {
        if (this.mImageSize == 0) {
            this.mImageSize = mViewGroup.getResources().getDimensionPixelOffset(R.dimen.media_item_image_height);
            this.mImageSize = (int) (mImageSize * 0.6f);
        }
        return this.mImageSize;
    }
}















