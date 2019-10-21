package com.zj.album.ui.folders;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import com.zj.album.PhotoAlbum;
import com.zj.album.R;
import com.zj.album.imageloader.impl.GlideLoader;
import com.zj.album.nHelpers.DataStore;
import com.zj.album.nModule.FolderInfo;
import com.zj.album.ui.base.BaseActivity;
import com.zj.album.ui.base.list.adapters.BaseAdapterDataSet;
import com.zj.album.ui.base.list.holders.BaseViewHolder;
import com.zj.album.ui.base.list.views.EmptyRecyclerView;

public class FolderActivity extends BaseActivity {

    @Override
    public int getContentView() {
        return R.layout.folder_activity;
    }

    @Override
    public void initData() {
        EmptyRecyclerView<FolderInfo> recyclerView = findViewById(R.id.folder_lv_file);
        recyclerView.setData(R.layout.folder_item_choose_file, false, DataStore.getFolderData(), new BaseAdapterDataSet<FolderInfo>() {
            @Override
            public void initData(BaseViewHolder holder, int position, FolderInfo data) {
                holder.setText(R.id.folder_item_tv_name, data.getParentName());
                holder.setText(R.id.folder_item_tv_count, PhotoAlbum.getString(R.string.pg_str_picture_count, data.getImageCounts()));
                ImageView iv = holder.getView(R.id.folder_item_iv_img);
                holder.getView(R.id.folder_item_select).setSelected(DataStore.isCurDisplayFolder(data.getId()));
                new GlideLoader().loadThumbnail(iv, iv.getMeasuredWidth(), R.mipmap.photo_nodata, data.getTopImgUri());
            }

            @Override
            public void onItemClick(int position, View v, @Nullable FolderInfo m) {
                DataStore.INSTANCE.setData(m);
                finish();
            }
        });
    }
}
