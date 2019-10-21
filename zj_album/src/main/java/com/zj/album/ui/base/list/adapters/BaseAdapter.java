package com.zj.album.ui.base.list.adapters;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.zj.album.ui.base.list.holders.BaseViewHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by ZJJ on 2018/4/4.
 */

@SuppressWarnings("WeakerAccess")
public abstract class BaseAdapter<T> extends BaseRecyclerAdapter<BaseViewHolder, T> {

    private final int resId;
    private LayoutInflater inflater;

    protected BaseAdapter(@LayoutRes int id) {
        resId = id;
    }

    protected BaseAdapter(@LayoutRes int id, List<T> data) {
        resId = id;
        change(data);
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        return new BaseViewHolder(this, inflater.inflate(resId, parent, false));
    }

    @Override
    public final void onBindViewHolder(@NotNull BaseViewHolder holder, int position) {
        initData(holder, position, getItem(position));
    }

    protected abstract void initData(BaseViewHolder holder, int position, T data);


}
