package com.zj.album.graphy.views;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * creat by zhaojie on 18.2.5.
 * <p>
 * 这个是ListView的基础Adapter类型，直接继承它实现默认方法即可；
 */

public abstract class IBaseAdapter<T> extends BaseAdapter {

    private List<T> infos;

    public void add(T info) {
        if (infos == null) {
            infos = new ArrayList<>();
        }
        infos.add(info);
    }


    public void setInfos(List<T> infos) {
        if (infos == null) {
            infos = new ArrayList<>();
        }
        this.infos = infos;
    }

    public void add(List<T> infos) {
        if (this.infos == null) {
            this.infos = new ArrayList<>();
        }
        if (infos == null) {
            return;
        }
        this.infos.addAll(infos);
    }

    public void remove(T info) {
        if (infos == null) {
            infos = new ArrayList<>();
            return;
        }
        infos.remove(info);
    }

    public void remove(int index) {
        if (infos == null) {
            infos = new ArrayList<>();
            return;
        }
        infos.remove(index);
    }

    public void clear() {
        if (infos == null) {
            return;
        } else {
            infos.clear();
        }
    }

    public List<T> getInfos() {
        return infos;
    }


    @Override
    public int getCount() {
        return infos == null ? 0 : infos.size();
    }

    @Override
    public T getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
