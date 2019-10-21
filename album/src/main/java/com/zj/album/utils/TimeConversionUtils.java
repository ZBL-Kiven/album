package com.zj.album.utils;

import java.util.Locale;

/**
 * @author yangji
 */
public class TimeConversionUtils {

    /**
     * 时间转换工具类
     *
     * @param mediaDuration 秒表时间转分钟
     * @return 比如视频时长
     */
    public static String getDuration(long mediaDuration) {
        long duration = mediaDuration / 1000;
        long minute = duration / 60;
        long second = duration % 60;
        return String.format(Locale.getDefault(), "%d:%d", minute, second);
    }
}
