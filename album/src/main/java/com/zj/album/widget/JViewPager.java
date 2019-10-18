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

    private boolean mLooper = false;
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

//    @Override
//    public void setAdapter(PagerAdapter adapter) {
//        JAdapter jAdapter;
//        if (adapter != null) {
//            jAdapter = new JAdapter(adapter);
//            jAdapter.setInfiniteRatio(mInfiniteRatio);
//            jAdapter.setLooper(mLooper);
//            super.setAdapter(jAdapter);
//        } else {
//            super.setAdapter(adapter);
//        }
//    }

//    @Override
//    public void setCurrentItem(int item) {
//        this.setCurrentItem(item, true);
//    }
//
//    @Override
//    public void setCurrentItem(int item, boolean smoothScroll) {
////        if (getAdapter() == null) {
////            super.setCurrentItem(item, smoothScroll);
////            return;
////        }
////        int size = getAdapter().getCount();
////        int current = getCurrentItem();
////        if (current <= 0) {
////            current = size * (mInfiniteRatio / 2);
////        }
////
////        //真实位置
////        int relIndex = current % size;
////
////        if (Math.abs(relIndex - item) >= 2) {
////            super.setCurrentItem(item * (mInfiniteRatio / 2), false);
////            return;
////        }
////
////        if (relIndex == item) {
////            //位置相同 不做处理
////            super.setCurrentItem(current, smoothScroll);
////        } else if (relIndex > item) {
////            super.setCurrentItem(getCurrentItem() - 1, smoothScroll);
////        } else {
////            super.setCurrentItem(getCurrentItem() + 1, smoothScroll);
////        }
//
//
//    }

    @Nullable
    @Override
    public PagerAdapter getAdapter() {
        return super.getAdapter() == null ? null : ((JAdapter) super.getAdapter()).getAdapter();
    }

//    public void setLooper(boolean mLooper) {
//        this.mLooper = mLooper;
//    }
//
//    public void setInfiniteRatio(int mInfiniteRatio) {
//        this.mInfiniteRatio = mInfiniteRatio;
//    }
}
