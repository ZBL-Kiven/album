package com.zj.album.ui.base.list.adapters;

import android.animation.Animator;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.zj.album.ui.base.list.ViewHelper;
import com.zj.album.ui.base.list.holders.BaseViewHolder;

import java.util.List;

/**
 * create by ZJJ on 18/6/1
 * <p>
 * if you need an animation with your list data loading,
 * use this adapter and build the method getAnimators();
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AnimationAdapter<T> extends BaseAdapter<T> {

    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;

    private boolean isFirstOnly = true;

    protected AnimationAdapter(@IdRes int id) {
        super(id);
    }

    protected AnimationAdapter(@IdRes int id, List<T> datas) {
        super(id, datas);
    }

    @Override
    protected void initData(BaseViewHolder holder, int position, T module) {
        int adapterPosition = holder.getAdapterPosition();
        if (!isFirstOnly || adapterPosition > mLastPosition) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setDuration(mDuration).start();
                anim.setInterpolator(mInterpolator);
            }
            mLastPosition = adapterPosition;
        } else {
            ViewHelper.clear(holder.itemView);
        }
        bindData(holder, position, module);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    protected abstract Animator[] getAnimators(View view);

    protected abstract void bindData(BaseViewHolder holder, int position, T module);

    public void setFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
    }
}
