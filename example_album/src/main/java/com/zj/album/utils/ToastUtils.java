package com.zj.album.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * Created by zhaojie on 2018/2/5.
 */
public class ToastUtils {

    private static Toast toast_utils;

    private ToastUtils() {
    }

    @SuppressLint("ShowToast")
    public static void show(Context c, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (toast_utils == null) {
            toast_utils = Toast.makeText(c, text, Toast.LENGTH_SHORT);
        }
        toast_utils.setText(text);
        toast_utils.show();
    }

    public static void cancel(){
        if (toast_utils != null) {
            toast_utils.cancel();
        }
    }
}