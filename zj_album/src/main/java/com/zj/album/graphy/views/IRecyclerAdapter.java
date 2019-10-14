package com.zj.album.graphy.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * creat by zhaojie on 18.2.5.
 * <p>
 * RecyclerViewAdapter的继承基类，该类不作为直接使用，只作为拓展类
 * <p>
 * 添加新的使用者请标注在下方；
 * <p>
 * 目前使用者为：IBaseRecyclerAdapter，
 */

public abstract class IRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> infos = new ArrayList<>();

    protected OnItemCLickListener onItemCLickListener;

    public void add(int index, T info) {
        if (infos == null) {
            infos = new ArrayList<>();
        }
        infos.add(index, info);
        notifyItemInserted(index);
        notifyItemRangeChanged(index, getItemCount());
    }

    public void add(T info) {
        if (infos == null) {
            infos = new ArrayList<>();
        }
        infos.add(info);
        notifyItemInserted(getCount() - 1);
    }

    public void add(List<T> infos) {
        if (this.infos == null) {
            this.infos = new ArrayList<>();
        }
        if (infos == null) {
            return;
        }
        this.infos.addAll(new ArrayList<>(infos));
        notifyDataSetChanged();
    }

    public void add(int index, List<T> infos) {
        if (this.infos == null) {
            this.infos = new ArrayList<>();
        }
        if (infos == null) {
            return;
        }
        this.infos.addAll(index, infos);
        notifyDataSetChanged();
    }

    public void remove(T info) {
        remove(infos.indexOf(info));
    }

    public void remove(int index) {
        if (index < 0) return;
        if (infos == null) {
            infos = new ArrayList<>();
            return;
        }
        infos.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, getItemCount());
    }

    public void clear() {
        if (infos == null) {
            return;
        } else {
            infos.clear();
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        if (infos == null) {
            infos = new ArrayList<>();
        }
        return infos;
    }

    public T getItem(int postion) {
        return this.infos.get(postion);
    }


    public int getCount() {
        return this.infos == null ? 0 : this.infos.size();
    }


    public IRecyclerAdapter(OnItemCLickListener listener) {
        this.onItemCLickListener = listener;
    }

    protected void setOnItemCLickListener(OnItemCLickListener onItemCLickListener) {
        this.onItemCLickListener = onItemCLickListener;
    }

    public interface OnItemCLickListener {
        void onItemClick(int postion, View view);
    }

}
