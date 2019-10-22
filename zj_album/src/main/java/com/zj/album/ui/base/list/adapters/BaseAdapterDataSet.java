package com.zj.album.ui.base.list.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import com.zj.album.ui.base.list.holders.BaseViewHolder;

import java.util.List;

/**
 * Created by ZJJ on 2018/4/9.
 */
@SuppressWarnings("unused")
public abstract class BaseAdapterDataSet<T> {

    public abstract void initData(BaseViewHolder holder, int position, T data, List<Object> payLoads);

    public void onItemClick(int position, View v, @Nullable T m) {

    }

    public void onItemLongClick(int position, View v, @Nullable T m) {

    }
}
