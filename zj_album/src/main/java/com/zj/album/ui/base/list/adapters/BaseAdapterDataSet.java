package com.zj.album.ui.base.list.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import com.zj.album.ui.base.list.holders.BaseViewHolder;

/**
 * Created by ZJJ on 2018/4/9.
 */
@SuppressWarnings("unused")
public abstract class BaseAdapterDataSet<T> {

    public abstract void initData(BaseViewHolder holder, int position, T data);

    public void onItemClick(int position, View v, @Nullable T m) {

    }

    public void onItemLongClick(int position, View v, @Nullable T m) {

    }
}
