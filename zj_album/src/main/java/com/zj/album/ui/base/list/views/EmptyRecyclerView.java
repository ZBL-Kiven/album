package com.zj.album.ui.base.list.views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.zj.album.ui.base.list.adapters.BaseAdapter;
import com.zj.album.ui.base.list.adapters.BaseAdapterDataSet;
import com.zj.album.ui.base.list.adapters.BaseRecyclerAdapter;
import com.zj.album.ui.base.list.holders.BaseViewHolder;
import com.zj.album.ui.base.list.listeners.ItemClickListener;

import java.util.List;

/**
 * @author ZJJ on 2018/4/9.
 */
@SuppressWarnings("unused")
public class EmptyRecyclerView<T> extends RecyclerView {

    private BaseAdapter<T> adapter;
    private BaseAdapterDataSet<T> mAdapterDataSet;

    public void canScroll(boolean isAllow) {
        setNestedScrollingEnabled(isAllow);
    }

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnDataChangeListener(BaseRecyclerAdapter.OnDataChange dataChangeListener) {
        this.adapter.setOnDataChangeListener(dataChangeListener);
    }

    @Override
    public BaseAdapter<T> getAdapter() {
        return adapter;
    }

    public void updateData(@Nullable List<T> data) {
        if (adapter != null) adapter.change(data);
    }

    public void setData(@LayoutRes int itemViewId, boolean isLoadMore, @Nullable List<T> data, @Nullable BaseAdapterDataSet<T> adapterDataSet) {
        this.mAdapterDataSet = adapterDataSet;
        setOverScrollMode(OVER_SCROLL_NEVER);
        if (getLayoutManager() == null)
            setLayoutManager(new LinearLayoutManager(getContext()));
        if (adapter == null) {
            adapter = new BaseAdapter<T>(itemViewId) {
                @Override
                protected void initData(BaseViewHolder holder, int position, T data, List<Object> payLoads) {
                    if (mAdapterDataSet != null) mAdapterDataSet.initData(holder, position, data, payLoads);
                }
            };
            setAdapter(adapter);
            if (adapterDataSet != null) {
                adapter.setOnItemClickListener(new ItemClickListener<T>() {
                    @Override
                    public void onItemClick(int position, View v, @Nullable T m) {
                        mAdapterDataSet.onItemClick(position, v, m);
                    }

                    @Override
                    public void onItemLongClick(int position, View v, @Nullable T m) {
                        mAdapterDataSet.onItemLongClick(position, v, m);
                    }
                });
            }
        }
        if (isLoadMore) adapter.add(data);
        else adapter.change(data);
    }
}
