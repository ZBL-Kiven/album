package com.zj.album.ui.base.list.listeners;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by ZJJ on 2018/4/4.
 */
@SuppressWarnings("unused")
public abstract class ItemClickListener<T> {

    public abstract void onItemClick(int position, View v, @Nullable T m);

    public void onItemLongClick(int position, View v, @Nullable T m) {

    }

}
