package com.zj.album.ui.base.list.adapters;

import android.support.v7.widget.RecyclerView;
import com.zj.album.ui.base.list.holders.BaseViewHolder;
import com.zj.album.ui.base.list.listeners.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJJ on 2018/4/4.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseRecyclerAdapter<VH extends BaseViewHolder, T> extends RecyclerView.Adapter<VH> {

    public BaseRecyclerAdapter() {
        data = new ArrayList<>();
    }

    public ItemClickListener<T> onClickListener;

    public void setOnItemClickListener(ItemClickListener<T> listener) {
        this.onClickListener = listener;
    }

    private List<T> data;

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getMaxPosition() {
        return getItemCount() - 1;
    }

    public List<T> getCopyData() {
        return new ArrayList<>(data);
    }

    public T getItem(int position) {
        return data.get(position);
    }

    public void add(T info) {
        if (info == null) return;
        this.data.add(info);
        notifyItemInserted(getMaxPosition());
        setDataChange();
    }

    public void add(T info, int position) {
        if (info == null) return;
        this.data.add(position, info);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getMaxPosition());
        setDataChange();
    }

    public void add(List<T> data) {
        if (data == null) return;
        this.data.addAll(data);
        notifyItemRangeInserted(getItemCount() - data.size(), this.getItemCount());
        setDataChange();
    }

    public void add(List<T> data, int position) {
        if (data == null) return;
        this.data.addAll(position, data);
        notifyItemRangeInserted(position, getMaxPosition());
        setDataChange();
    }

    public void remove(T info) {
        if (info == null) return;
        int position = data.indexOf(info);
        this.data.remove(info);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getMaxPosition());
        setDataChange();
    }

    public void remove(int position) {
        if (getItemCount() <= position) return;
        this.data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getMaxPosition());
        setDataChange();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
        setDataChange();
    }

    public void change(List<T> data) {
        clear();
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        notifyItemRangeChanged(0, getMaxPosition());
        setDataChange();
    }

    public void setDataChange() {
        if (onDataChange != null)
            onDataChange.onChange(this);
    }

    private OnDataChange onDataChange;

    public void setOnDataChangeListener(OnDataChange dataChangeListener) {
        this.onDataChange = dataChangeListener;
    }

    public interface OnDataChange {
        void onChange(BaseRecyclerAdapter adapter);
    }
}
