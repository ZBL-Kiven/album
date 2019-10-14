package com.zj.album.preview_banner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zj.album.preview_banner.transformer.PageTransformer;
import com.zj.album.preview_banner.transformer.TransitionEffect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PreviewBanner extends RelativeLayout implements BannerViewPager.AutoPlayDelegate, BannerViewPager.OnPageChangeListener {
    private static final int VEL_THRESHOLD = 400;
    private BannerViewPager mViewPager;
    private List<View> mHackyViews;
    private List<View> mViews;
    private boolean mAutoPlayAble = true;
    private int mAutoPlayInterval = 800;
    private int mPageChangeDuration = 500;
    private AutoPlayTask mAutoPlayTask;
    private int mPageScrollPosition;
    private float mPageScrollPositionOffset;
    private TransitionEffect mTransitionEffect;
    private ImageView mPlaceholderIv;
    private List<?> mModels;
    private Adapter mAdapter;
    private int mOverScrollMode = OVER_SCROLL_ALWAYS;
    private BannerViewPager.OnPageChangeListener mOnPageChangeListener;
    private boolean mAllowUserScrollable = true;

    public PreviewBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultAttrs();
    }

    private void initDefaultAttrs() {
        mAutoPlayTask = new AutoPlayTask(this);
        mTransitionEffect = TransitionEffect.Default;
    }

    /**
     * 设置页码切换过程的时间长度
     *
     * @param duration 页码切换过程的时间长度
     */
    public void setPageChangeDuration(int duration) {
        if (duration >= 0 && duration <= 1000) {
            mPageChangeDuration = duration;
            if (mViewPager != null) {
                mViewPager.setPageChangeDuration(duration);
            }
        }
    }

    /**
     * 设置自动轮播的时间间隔
     */
    public void setAutoPlayInterval(int autoPlayInterval) {
        mAutoPlayInterval = autoPlayInterval;
    }

    /**
     * 设置每一页的控件、数据模型和文案
     *
     * @param views  每一页的控件集合
     * @param models 每一页的数据模型集合
     */
    public void setDatas(List<View> views, List<?> models) {
        if (mAutoPlayAble && views.size() < 3 && mHackyViews == null) {
            mAutoPlayAble = false;
        }
        mModels = models;
        mViews = views;
        initViewPager();
        removePlaceholder();
    }

    /**
     * 设置布局资源id、数据模型和文案
     *
     * @param layoutResId item布局文件资源id
     * @param models      每一页的数据模型集合
     */
    public void setData(@LayoutRes int layoutResId, List<?> models) {
        if (models == null || models.size() == 0) {
            if (mViewPager != null) mViewPager.removeAllViews();
            if (mViews != null) mViews.clear();
            if (mHackyViews != null) mHackyViews.clear();
            return;
        }
        mViews = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            mViews.add(getResView(layoutResId));
        }
        if (mAutoPlayAble && mViews.size() < 3) {
            mHackyViews = new ArrayList<>(mViews);
            mHackyViews.add(getResView(layoutResId));
            if (mHackyViews.size() == 2) {
                mHackyViews.add(getResView(layoutResId));
            }
        }
        setDatas(mViews, models);
    }

    private View getResView(int id) {
        if (id != -1) {
            return View.inflate(getContext(), id, null);
        } else {
            ImageView v = new ImageView(getContext());
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            params.width = ViewPager.LayoutParams.MATCH_PARENT;
            params.height = ViewPager.LayoutParams.MATCH_PARENT;
            v.setLayoutParams(params);
            v.setClickable(true);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return v;
        }
    }

    /**
     * 设置数据模型和文案，布局资源默认为ImageView
     *
     * @param models 每一页的数据模型集合
     */
    public void setData(List<?> models) {
        setData(-1, models);
    }

    /**
     * 设置是否允许用户手指滑动
     *
     * @param allowUserScrollable true表示允许跟随用户触摸滑动，false反之
     */
    public void setAllowUserScrollable(boolean allowUserScrollable) {
        mAllowUserScrollable = allowUserScrollable;
        if (mViewPager != null) {
            mViewPager.setAllowUserScrollable(mAllowUserScrollable);
        }
    }

    /**
     * 添加ViewPager滚动监听器
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 获取广告页面总个数
     */
    public int getItemCount() {
        return mViews == null ? 0 : mViews.size();
    }

    public List<? extends View> getViews() {
        return mViews;
    }

    public View getItemView(int position) {
        return mViews == null ? null : mViews.get(position);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setOverScrollMode(int overScrollMode) {
        mOverScrollMode = overScrollMode;
        if (mViewPager != null) {
            mViewPager.setOverScrollMode(mOverScrollMode);
        }
    }

    private void initViewPager() {
        if (mViewPager != null && this.equals(mViewPager.getParent())) {
            this.removeView(mViewPager);
        }
        mViewPager = new BannerViewPager(getContext());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new PageAdapter());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOverScrollMode(mOverScrollMode);
        mViewPager.setAllowUserScrollable(mAllowUserScrollable);
        mViewPager.setPageTransformer(true, PageTransformer.getPageTransformer(mTransitionEffect));
        addView(mViewPager, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setPageChangeDuration(mPageChangeDuration);

        if (mAutoPlayAble) {
            mViewPager.setAutoPlayDelegate(this);

            int zeroItem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % mViews.size();
            mViewPager.setCurrentItem(zeroItem);

            startAutoPlay();
        }
    }

    private void removePlaceholder() {
        if (mPlaceholderIv != null && this.equals(mPlaceholderIv.getParent())) {
            removeView(mPlaceholderIv);
            mPlaceholderIv = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAutoPlayAble) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoPlay();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    startAutoPlay();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setCurrentItem(int item) {
        if (mViewPager == null || mViews == null || item > getItemCount() - 1) {
            return;
        }

        if (mAutoPlayAble) {
            int realCurrentItem = mViewPager.getCurrentItem();
            int currentItem = realCurrentItem % mViews.size();
            int offset = item - currentItem;

            // 这里要使用循环递增或递减设置，否则会ANR
            if (offset < 0) {
                for (int i = -1; i >= offset; i--) {
                    mViewPager.setCurrentItem(realCurrentItem + i, false);
                }
            } else if (offset > 0) {
                for (int i = 1; i <= offset; i++) {
                    mViewPager.setCurrentItem(realCurrentItem + i, false);
                }
            }
            startAutoPlay();
        } else {
            mViewPager.setCurrentItem(item, false);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else if (visibility == INVISIBLE) {
            stopAutoPlay();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoPlay();
    }

    public void startAutoPlay() {
        stopAutoPlay();
        if (mAutoPlayAble && mAutoPlayInterval > 300) {
            postDelayed(mAutoPlayTask, mAutoPlayInterval);
        }
    }

    public void stopAutoPlay() {
        if (mAutoPlayAble) {
            removeCallbacks(mAutoPlayTask);
        }
    }

    /**
     * 设置页面切换换动画
     */
    public void setTransitionEffect(TransitionEffect effect) {
        mTransitionEffect = effect;
        if (mViewPager != null) {
            initViewPager();
            if (mHackyViews == null) {
                BannerUtil.resetPageTransformer(mViews);
            } else {
                BannerUtil.resetPageTransformer(mHackyViews);
            }
        }
    }

    public void setPageMargin(int margin) {
        mViewPager.setMargin(margin);
    }

    /**
     * 切换到下一页
     */
    private void switchToNextPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    @Override
    public void handleAutoPlayActionUpOrCancel(float xVelocity) {
        if (mPageScrollPosition < mViewPager.getCurrentItem()) {
            // 往右滑
            if (xVelocity > VEL_THRESHOLD || (mPageScrollPositionOffset < 0.7f && xVelocity > -VEL_THRESHOLD)) {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition);
            } else {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition + 1);
            }
        } else {
            // 往左滑
            if (xVelocity < -VEL_THRESHOLD || (mPageScrollPositionOffset > 0.3f && xVelocity < VEL_THRESHOLD)) {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition + 1);
            } else {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        position = position % mViews.size();
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPageScrollPosition = position;
        mPageScrollPositionOffset = positionOffset;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position % mViews.size(), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViews == null ? 0 : (mAutoPlayAble ? Integer.MAX_VALUE : mViews.size());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position <= 0) return null;
            final int finalPosition = position % mViews.size();
            View view;
            if (mHackyViews == null) {
                view = mViews.get(finalPosition);
            } else {
                view = mHackyViews.get(position % mHackyViews.size());
            }

            if (container.equals(view.getParent())) {
                container.removeView(view);
            }

            if (mAdapter != null) {
                mAdapter.fillBannerItem(PreviewBanner.this, view, mModels == null ? null : mModels.get(finalPosition), finalPosition);
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private static class AutoPlayTask implements Runnable {
        private final WeakReference<PreviewBanner> mBanner;

        private AutoPlayTask(PreviewBanner banner) {
            mBanner = new WeakReference<>(banner);
        }

        @Override
        public void run() {
            PreviewBanner banner = mBanner.get();
            if (banner != null) {
                banner.switchToNextPage();
                banner.startAutoPlay();
            }
        }
    }

    public interface Adapter {
        void fillBannerItem(PreviewBanner banner, View view, Object model, int position);
    }
}