package com.zj.album.graphy.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * creat by zhaojie on 18.2.5.
 * <p>
 * RecyclerView所需要的Holder继承基类，需要时直接继承此类实现相应方法即可；
 * <p>
 * 起到统一代码结构以便于管理的作用；
 */

public abstract class RecyclerHolder extends RecyclerView.ViewHolder {

    public RecyclerHolder(View itemView) {
        super(itemView);
    }

    public <T extends View> T f(int res) {
        return (T) itemView.findViewById(res);
    }

}
