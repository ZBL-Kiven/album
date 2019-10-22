package com.zj.album.ui.folders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import android.widget.ImageView;
import com.zj.album.PhotoAlbum;
import com.zj.album.R;
import com.zj.album.imageloader.impl.GlideLoader;
import com.zj.album.nHelpers.DataStore;
import com.zj.album.nModule.FileInfo;
import com.zj.album.nModule.FolderInfo;
import com.zj.album.ui.base.BaseActivity;
import com.zj.album.ui.base.list.adapters.BaseAdapter;
import com.zj.album.ui.base.list.adapters.BaseAdapterDataSet;
import com.zj.album.ui.base.list.holders.BaseViewHolder;
import com.zj.album.ui.base.list.views.EmptyRecyclerView;

import java.util.List;

public class FolderActivity extends BaseActivity {

    private boolean isStopHandleData;

    @Override
    public int getContentView() {
        return R.layout.folder_activity;
    }

    @Override
    public void initListener() {
        isStopHandleData = false;
        findViewById(R.id.folder_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onDataGot(List<FileInfo> lst, @NonNull String curAccessKey) {
        super.onDataGot(lst, curAccessKey);
        if (isStopHandleData) return;
        final EmptyRecyclerView<FolderInfo> recyclerView = findViewById(R.id.folder_lv_file);
        recyclerView.setData(R.layout.folder_item_choose_file, false, DataStore.getFolderData(), new BaseAdapterDataSet<FolderInfo>() {
            @Override
            public void initData(BaseViewHolder holder, int position, FolderInfo data, List<Object> payLoads) {
                holder.getView(R.id.folder_item_select).setSelected(DataStore.isCurDisplayFolder(data.getId()));
                if (payLoads != null && payLoads.size() > 0) return;
                holder.setText(R.id.folder_item_tv_name, data.getParentName());
                holder.setText(R.id.folder_item_tv_count, PhotoAlbum.getString(R.string.pg_str_picture_count, data.getImageCounts()));
                ImageView iv = holder.getView(R.id.folder_item_iv_img);
                new GlideLoader().loadThumbnail(iv, iv.getMeasuredWidth(), R.mipmap.photo_nodata, data.getTopImgUri());
            }

            @Override
            public void onItemClick(int position, View v, @Nullable FolderInfo m) {
                isStopHandleData = true;
                DataStore.INSTANCE.setData(m);
                BaseAdapter adapter = recyclerView.getAdapter();
                if (adapter != null) {
                    adapter.notifyItemRangeChanged(0, adapter.getMaxPosition(), 0);
                }
                finish();
            }
        });
    }
}
