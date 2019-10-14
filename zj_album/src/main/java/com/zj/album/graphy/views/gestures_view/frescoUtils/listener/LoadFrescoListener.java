package com.zj.album.graphy.views.gestures_view.frescoUtils.listener;

import android.graphics.Bitmap;

public interface LoadFrescoListener {
    void onSuccess(Bitmap bitmap);

    void onFail(Throwable failureCause);
}