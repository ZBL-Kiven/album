package com.zj.album.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

/**
 * @author yangji
 */
public class JAdapter extends PagerAdapter {

    private PagerAdapter mAdapter;
    private boolean looper = true;
    private SparseArray<View> viewArray;
    private int infiniteRatio = 500;

    public JAdapter(PagerAdapter adapter) {
        this.mAdapter = adapter;
        viewArray = new SparseArray<>();
    }

    @Override
    public int getCount() {
        int count;
        if (looper) {
            if (mAdapter.getCount() == 0) {
                count = 0;
            } else {
                count = mAdapter.getCount() * infiniteRatio;
            }
        } else {
            count = mAdapter.getCount();
        }
        return count;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        int realPosition = position;

        if (looper && mAdapter.getCount() != 0) {
            realPosition = position % mAdapter.getCount();
        }

        View item = (View) mAdapter.instantiateItem(container, realPosition);

        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = container.getChildAt(i);
            if (isViewFromObject(child, item)) {
                viewArray.put(realPosition, child);
                break;
            }
        }
        return item;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return mAdapter.isViewFromObject(view, o);
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        int realPosition = position;

        if (looper && mAdapter.getCount() != 0) {
            realPosition = position % mAdapter.getCount();
        }

        mAdapter.destroyItem(container, realPosition, object);
        viewArray.remove(realPosition);
    }

    public void setInfiniteRatio(int infiniteRatio) {
        this.infiniteRatio = infiniteRatio;
    }

    public void setLooper(boolean isLooper) {
        this.looper = isLooper;
    }

    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 为以后做扩展
     *
     * @param position 为以后做扩展
     * @return
     */
    public View getViewAtPosition(int position) {
        int realPosition = position;
        if (looper && mAdapter.getCount() != 0) {
            realPosition = position % mAdapter.getCount();
        }
        return viewArray.get(realPosition);
    }
}
