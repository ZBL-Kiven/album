package com.zj.album.utils;

import android.content.Context;


/**
 * Created by zhaojie on 2017/10/10.
 */
public class DisplayUtils {

    /**
     * 将dip或dp值转换为px值
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (dipValue * (scale / 160) + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取屏幕宽高
     */
    public static int[] getDefaultDisplayMetrics(Context context) {
        int[] i = new int[2];
        i[0] = context.getResources().getDisplayMetrics().widthPixels;
        i[1] = context.getResources().getDisplayMetrics().heightPixels;
        return i;
    }

    /**
     * dp转换成px
     */
    public static int dp2px(Context context, Float dpValue) {
        Float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

