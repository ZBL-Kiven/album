package com.zj.album.graphy.views.gestures_view.zoomable;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Great by zhaojie on 2017.10.31
 * <p>
 * 手势监听区分（单点，双击，拖动，缩放等）
 */
public class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int DURATION_MS = 300;
    private static final int DOUBLE_TAP_SCROLL_THRESHOLD = 20;

    private final WeakReference<ZoomableDraweeView> mDraweeView;
    private final PointF mDoubleTapViewPoint = new PointF();
    private final PointF mDoubleTapImagePoint = new PointF();
    private float mDoubleTapScale = 1;
    private boolean mDoubleTapScroll = false;
    private View.OnClickListener listener;
    private boolean isZoomScale = false;

    public DoubleTapGestureListener(ZoomableDraweeView zoomableDraweeView, View.OnClickListener l) {
        this.listener = l;
        mDraweeView = new WeakReference<>(zoomableDraweeView);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        isZoomScale = false;
        if (listener != null) {
            mDraweeView.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isZoomScale)
                        listener.onClick(mDraweeView.get());
                }
            }, DURATION_MS + 20);
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        isZoomScale = true;
        AbstractAnimatedZoomableController zc =
                (AbstractAnimatedZoomableController) mDraweeView.get().getZoomableController();
        PointF vp = new PointF(e.getX(), e.getY());
        PointF ip = zc.mapViewToImage(vp);
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDoubleTapViewPoint.set(vp);
                mDoubleTapImagePoint.set(ip);
                mDoubleTapScale = zc.getScaleFactor();
                break;
            case MotionEvent.ACTION_MOVE:
                mDoubleTapScroll = mDoubleTapScroll || shouldStartDoubleTapScroll(vp);
                if (mDoubleTapScroll) {
                    float scale = calcScale(vp);
                    zc.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mDoubleTapScroll) {
                    float scale = calcScale(vp);
                    zc.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint);
                } else {
                    final float maxScale = zc.getMaxScaleFactor();
                    final float minScale = zc.getMinScaleFactor();
                    if (zc.getScaleFactor() < (maxScale + minScale) / 2) {
                        //放大

                        zc.zoomToPoint(maxScale, ip, vp, DefaultZoomableController.LIMIT_ALL, DURATION_MS, null);
                    } else {
                        //缩小
                        zc.zoomToPoint(minScale, ip, vp, DefaultZoomableController.LIMIT_ALL, DURATION_MS, null);
                    }
                }
                mDoubleTapScroll = false;
                break;
        }
        return true;
    }

    private boolean shouldStartDoubleTapScroll(PointF viewPoint) {
        double dist = Math.hypot(
                viewPoint.x - mDoubleTapViewPoint.x,
                viewPoint.y - mDoubleTapViewPoint.y);
        return dist > DOUBLE_TAP_SCROLL_THRESHOLD;
    }

    private float calcScale(PointF currentViewPoint) {
        float dy = (currentViewPoint.y - mDoubleTapViewPoint.y);
        float t = 1 + Math.abs(dy) * 0.001f;
        return (dy < 0) ? mDoubleTapScale / t : mDoubleTapScale * t;
    }

    public void clear() {
        mDraweeView.clear();
        listener = null;
    }
}