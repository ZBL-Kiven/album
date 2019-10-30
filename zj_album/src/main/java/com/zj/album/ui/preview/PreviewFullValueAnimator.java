package com.zj.album.ui.preview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author ZJJ on 2019.10.24
 * */
@SuppressWarnings("unused")
class PreviewFullValueAnimator extends ValueAnimator {

    private boolean isFull;
    private float curDuration;
    private boolean isCancel;

    private FullAnimatorListener listener;

    void start(boolean isFull) {
        if (isRunning()) cancel();
        this.isFull = isFull;
        super.start();
    }

    @Override
    public void cancel() {
        removeAllListeners();
        if (listener != null) listener = null;
        isCancel = true;
        super.cancel();
    }

    PreviewFullValueAnimator(FullAnimatorListener l) {
        this.listener = l;
        setFloatValues(0.0f, 1.0f);
        setInterpolator(new AccelerateDecelerateInterpolator());
        addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (curDuration != 0) curDuration = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                curDuration = 0;
                if (isCancel) return;
                if (listener != null)
                    listener.onAnimEnd(animation, isFull);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                curDuration = 0;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                curDuration = 0;
            }
        });

        addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isCancel) return;
                if (listener != null) {
                    float duration = (float) animation.getAnimatedValue();
                    float offset = duration - curDuration;
                    listener.onDurationChange(animation, offset, isFull);
                    curDuration = duration;
                }
            }
        });
    }

    public void setAnimatorListener(FullAnimatorListener listener) {
        this.listener = listener;
    }

    public interface FullAnimatorListener {

        void onDurationChange(ValueAnimator animation, float duration, boolean isFull);

        void onAnimEnd(Animator animation, boolean isFull);
    }
}

