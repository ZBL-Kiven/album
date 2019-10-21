package com.zj.album.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.zj.album.widget.image.ImageViewTouch;


/**
 * @author yangji
 */
public class JViewPager extends ViewPager {

    public abstract static class PageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    private boolean mLooper = true;
    private int mInfiniteRatio = 500;

    public JViewPager(@NonNull Context context) {
        super(context);
    }

    public JViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean cv, int dx, int x, int y) {
        if (v instanceof ImageViewTouch) {
            return ((ImageViewTouch) v).canScroll(dx) || super.canScroll(v, cv, dx, x, y);
        }
        return super.canScroll(v, cv, dx, x, y);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        JAdapter jAdapter;
        if (adapter != null) {
            jAdapter = new JAdapter(adapter);
            jAdapter.setInfiniteRatio(mInfiniteRatio);
            jAdapter.setLooper(mLooper);
            super.setAdapter(jAdapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    @Override
    public void addOnPageChangeListener(@NonNull final OnPageChangeListener listener) {
        super.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                listener.onPageScrolled(i, v, i1);
            }

            @Override
            public void onPageSelected(int i) {
                if (getAdapter() != null) {
                    int count = getCount();
                    listener.onPageSelected(i % count);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (getAdapter() != null) {
                    listener.onPageScrollStateChanged(i % getAdapter().getCount());
                }
            }
        });
    }

    public int getCount() {
        PagerAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }
        return adapter.getCount();
    }

    private int getRelCount() {
        PagerAdapter adapter = super.getAdapter();
        if (adapter == null) {
            return 0;
        }
        return adapter.getCount();
    }

    @Override
    public int getCurrentItem() {
        return getRelCurrentItem() % getCount();
    }

    public int getRelCurrentItem() {
        return super.getCurrentItem();
    }


    @Override
    public void setCurrentItem(int item) {
        this.setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (getAdapter() == null || !mLooper) {
            super.setCurrentItem(item, smoothScroll);
            return;
        }
        int size = getAdapter().getCount();
        int current = getRelCurrentItem();
        if (current <= 0) {
            current = size * (mInfiniteRatio / 2);
        }

        if (getRelCurrentItem() == 0 && item == 0) {
            super.setCurrentItem(current, smoothScroll);
            return;
        }

        //真实位置
        int relIndex = current % size;

        if (relIndex == item) {
            //位置相同 不做处理
        } else if (relIndex > item) {
            super.setCurrentItem(getRelCurrentItem() - 1, smoothScroll);
        } else {
            super.setCurrentItem(getRelCurrentItem() + 1, smoothScroll);
        }

    }

    @Nullable
    @Override
    public PagerAdapter getAdapter() {
        return super.getAdapter() == null ? null : ((JAdapter) super.getAdapter()).getAdapter();
    }

    public void setLooper(boolean mLooper) {
        this.mLooper = mLooper;
    }

    public void setInfiniteRatio(int mInfiniteRatio) {
        this.mInfiniteRatio = mInfiniteRatio;
    }
}
