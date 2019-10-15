package com.zj.album.preview_banner;

import android.content.Context;
import android.widget.Scroller;

public class BannerScroller extends Scroller {
    private int mDuration = 1000;

    public BannerScroller(Context context) {
        super(context);
    }

    public BannerScroller(Context context, int time) {
        super(context);
        this.mDuration = time;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy,
                            int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int time) {
        mDuration = time;
    }

    public int getmDuration() {
        return mDuration;
    }
}