package com.zj.album.imageloader.utils;

import android.graphics.BitmapFactory;
import android.graphics.Point;


/**
 * @author yangji
 */
public class ImageEvaluate {

    public static Point getBitmapSize(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int width = options.outWidth;
            int height = options.outHeight;
            //这里需要评估图片大小 如果太大需要进行缩放

            if (width / 1080 > 1) {
                width = width / 2;
                height = height / 2;
            }

            return new Point(width, height);
        } catch (Exception e) {
            return new Point(0, 0);
        }
    }
}
