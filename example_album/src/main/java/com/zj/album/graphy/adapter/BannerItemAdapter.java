package com.zj.album.graphy.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.zj.album.R;
import com.zj.album.preview_banner.PreviewBanner;
import com.zj.album.preview_banner.ViewPager;
import com.zj.album.preview_banner.transformer.TransitionEffect;
import com.zj.album.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/27.
 * <p>
 * 使用该适配器，可使banner合理管理自己的内存，通过设置一个最大gc尺寸来使用Banner；
 * 适用于大图片或大量图片需要展示的情况，也适配适用于极大批量View的回收加载
 */

@SuppressWarnings("FieldCanBeLocal")
public class BannerItemAdapter implements PreviewBanner.Adapter {

    private OnPageChange pageChange;
    private int old_curPosition, cur_curPosition, cur_nextPosition, displayChange, initPosition;
    private PreviewBanner banner;
    private int maxGcSize = 3;
    private List<?> datas;
    private boolean isFirst = true;
    //当前选择的item的index
    private int curSelectedPosition;
    private TouchOrientation curOrientation = null;

    public void clear() {
        datas.clear();
        for (int i = 0; i < banner.getViewPager().getChildCount(); i++) {
            if (banner.getViewPager().getChildAt(i) instanceof ViewGroup)
                ((ViewGroup) banner.getViewPager().getChildAt(i)).removeAllViews();
        }
        System.gc();
    }

    private enum TouchOrientation {
        left, right
    }

    public BannerItemAdapter(int maxGc_Size, int curSelectPosition, PreviewBanner b, List<?> datas, OnPageChange change) {
        this.pageChange = change;
        this.banner = b;
        this.maxGcSize = Math.max(maxGc_Size, maxGcSize);
        this.curSelectedPosition = initPosition = curSelectPosition;
        this.displayChange = curSelectPosition;
        this.datas = datas;
        boolean isCanScrollAuto = datas.size() > 1;
        if (!isCanScrollAuto) {
            maxGcSize = datas.size();
            banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    displayChange = position;
                    if (pageChange != null) pageChange.onDisplayChange(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            banner.setAdapter(new PreviewBanner.Adapter() {
                @Override
                public void fillBannerItem(PreviewBanner banner, View view, Object model, int position) {
                    if (pageChange != null) pageChange.onChange(position, view);
                }
            });
            banner.setData(R.layout.banner_preview_item2, datas);
        } else {
            banner.setAdapter(this);
            banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (isFirst) {
                        return;
                    }
                    getRealPosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            List<String> lst = new ArrayList<>();
            for (int i = 0; i < maxGcSize; i++) {
                lst.add("asd  " + i);
            }
            banner.setData(R.layout.banner_preview_item2, lst);
        }
        banner.setAutoPlayInterval(0);
        //配置一个动画
        banner.setTransitionEffect(TransitionEffect.Zoom);
        banner.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (change != null)
            change.onDisplayChange(curSelectPosition);
        banner.setPageMargin(DisplayUtils.dip2px(banner.getContext(), 10));

        banner.setAllowUserScrollable(isCanScrollAuto);
    }

    @Override
    public void fillBannerItem(PreviewBanner banner, View view, Object model, int position) {
        if (isFirst) {
            if (pageChange != null) {
                int offset = 0;
                if (position != 0)
                    offset = position == maxGcSize - 1 ? -1 : 2;
                curSelectedPosition += offset;
                if (curSelectedPosition < 0) curSelectedPosition = datas.size() - 1;
                if (curSelectedPosition >= datas.size()) curSelectedPosition -= datas.size();
                pageChange.onChange(curSelectedPosition, banner.getItemView(position));
            }
            if (position == 1) {
                isFirst = false;
                curSelectedPosition = initPosition;
            }
        } else {
            cur_nextPosition = position;
        }
    }

    public interface OnPageChange {
        void onChange(int position, View view);

        void onDisplayChange(int dataPosition);
    }

    private void getRealPosition(int position) {
        cur_curPosition = position;
        //划到0时，next为最大值，载入banner最后一个视图，但是展示的是实际数据的前一个
        if (maxGcSize == datas.size()) curSelectedPosition = cur_curPosition;
        boolean isRightOver = (cur_curPosition == maxGcSize - 1) && (old_curPosition == 0);//右边划到头了，加载左边第一个的时候
        boolean isLeftOver = (cur_curPosition == 0 && old_curPosition == maxGcSize - 1);//左边划到头了，加载最后一个
        /*
         * @param curOrientation 在此之后有效
         * @param curSelectedPosition 在此之后有效
         * */
        if (cur_curPosition != old_curPosition) {
            if ((cur_curPosition > old_curPosition || isLeftOver) && !isRightOver) {
                //向左滑，需要加载的应该是下一个
                curOrientation = TouchOrientation.left;
            } else {
                //向右滑，需要加载的是上一个
                curOrientation = TouchOrientation.right;
            }
            if (curOrientation == TouchOrientation.left) {
                displayChange++;
                //实际数据其实是预加载的下一个，而上一个在此时是肯定已加载完成的
                curSelectedPosition = displayChange + 1;
            } else {
                displayChange--;
                //实际数据其实是预加载的上一个，而下一个其实就是显示的数据本身
                curSelectedPosition = displayChange - 1;
            }
            //  如果下一个要显示的index已经超出约定值的差值
            if (curSelectedPosition >= datas.size()) {
                curSelectedPosition -= datas.size();
            }
            if (curSelectedPosition < 0) {
                curSelectedPosition = datas.size() + curSelectedPosition;
            }
            if (displayChange >= datas.size()) displayChange = 0;
            if (displayChange < 0) displayChange = datas.size() - 1;
        }
        //这里载入实际index的下一个，可能在前也可能在后
        if (pageChange != null) {
            pageChange.onChange(curSelectedPosition, banner.getItemView(cur_nextPosition));
            pageChange.onDisplayChange(displayChange);
        }
        //数据倒换,position在此之后不作参考
        old_curPosition = cur_curPosition;
    }

    public int getDisplayPosition() {
        return displayChange;
    }
}
