package com.zj.album.graphy.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;


/**
 * creat by zhaojie on 18.2.5.
 * <p>
 * 所有的RecyclerViewAdapter直接继承此类，实现默认方法即可；
 */

public abstract class IBaseRecyclerAdapter<T, VH extends RecyclerHolder> extends IRecyclerAdapter<T, VH> {


    public IBaseRecyclerAdapter(OnItemCLickListener listener) {
        super(listener);
    }

    @NotNull
    @Override
    public abstract VH onCreateViewHolder(@NotNull ViewGroup parent, int viewType);

    @Override
    public int getItemCount() {
        return getCount();
    }
}
