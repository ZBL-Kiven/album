package com.zj.album.ui.views;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.zj.album.R;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZJJ on 2018/7/3.
 */

@SuppressWarnings({"unused", "unchecked"})
public class BaseLoadingView extends FrameLayout {

    public BaseLoadingView(Context context) {
        this(context, null, 0);
    }

    public BaseLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private static final int defaultAnimationDuration = 400;

    private DisplayMode oldMode = DisplayMode.none;
    private Map<DisplayMode, Float> disPlayViews;
    private View rootView;
    private View noData, noNetwork;
    private ProgressBar loading;
    private TextView tvHint, tvRefresh;
    private CallRefresh refresh;
    private int bgColor;
    private int bgColorOnAct;
    private int needBackgroundColor, oldBackgroundColor;
    private int noDataRes = -1;
    private int noNetworkRes = -1;
    private int loadingRes = -1;
    private int hintTextColor, refreshTextColor;
    private ArgbEvaluator argbEvaluator;
    private boolean refreshEnable = true;
    private boolean refreshEnableWithView = false;

    private BaseLoadingValueAnimator valueAnimator;

    public void setRefreshEnable(boolean enable) {
        this.refreshEnable = enable;
    }

    public interface CallRefresh {
        void onCallRefresh();
    }

    public enum DisplayMode {
        none(0), loading(1), noData(2), noNetwork(3), normal(4);

        private final int value;

        DisplayMode(int value) {
            this.value = value;
        }
    }

    /**
     * when you set mode as noData/noNetwork ,
     * you can get the event when this view was clicked
     * and you can refresh content  when the  "onCallRefresh()" callback
     */
    public void setRefreshListener(CallRefresh refresh) {
        this.refresh = refresh;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshEnable && refreshEnableWithView && BaseLoadingView.this.refresh != null) {
                    BaseLoadingView.this.refresh.onCallRefresh();
                }
            }
        });
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @Nullable TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BaseLoadingView);
            try {
                bgColor = array.getColor(R.styleable.BaseLoadingView_backgroundFill, ContextCompat.getColor(context, R.color.loading_color_background));
                bgColorOnAct = array.getColor(R.styleable.BaseLoadingView_backgroundOnAct, ContextCompat.getColor(context, R.color.loading_color_background_float));
                noDataRes = array.getResourceId(R.styleable.BaseLoadingView_noDataRes, -1);
                noNetworkRes = array.getResourceId(R.styleable.BaseLoadingView_noNetworkRes, -1);
                loadingRes = array.getResourceId(R.styleable.BaseLoadingView_loadingRes, -1);
                hintTextColor = array.getColor(R.styleable.BaseLoadingView_hintColor, -1);
                refreshTextColor = array.getColor(R.styleable.BaseLoadingView_refreshTextColor, -1);
            } catch (Exception ignore) {
            } finally {
                array.recycle();
            }
        }
        initView(context);
    }

    private void initView(Context context) {
        rootView = inflate(context, R.layout.loading_view, this);
        noData = f(R.id.blv_vNoData);
        noNetwork = f(R.id.blv_vNoNetwork);
        loading = f(R.id.blv_pb);
        tvHint = f(R.id.blv_tvHint);
        tvRefresh = f(R.id.blv_tvRefresh);
        disPlayViews = new HashMap<>();
        if (hintTextColor > 0)
            tvHint.setTextColor(hintTextColor);
        if (refreshTextColor > 0)
            tvRefresh.setTextColor(refreshTextColor);
        resetUi();
        argbEvaluator = new ArgbEvaluator();
    }

    private BaseLoadingAnimatorListener listener = new BaseLoadingAnimatorListener() {

        @Override
        public void onDurationChange(ValueAnimator animation, float offset, DisplayMode mode, boolean isShowOnAct) {
            synchronized (BaseLoadingView.this) {
                onAnimationFraction(animation.getAnimatedFraction(), offset, mode);
            }
        }

        @Override
        public void onAnimEnd(Animator animation, DisplayMode mode, boolean isShowOnAct) {
            synchronized (BaseLoadingView.this) {
                onAnimationFraction(1.0f, 1.0f, mode);
            }
        }
    };

    /**
     * @param drawableRes must be an animatorDrawable in progressBar;
     * @link call resetUi() after set this
     */
    public BaseLoadingView setLoadingDrawable(int drawableRes) {
        this.loadingRes = drawableRes;
        return this;
    }

    //call resetUi() after set this
    public BaseLoadingView setNoDataDrawable(int drawableRes) {
        this.noDataRes = drawableRes;
        return this;
    }

    //call resetUi() after set this
    public BaseLoadingView setNoNetworkDrawable(int drawableRes) {
        this.noNetworkRes = drawableRes;
        return this;
    }

    //reset loading/noData/noNetwork drawable
    private void resetUi() {
        if (loadingRes > 0) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), loadingRes);
            if (drawable != null) {
                Rect rect = loading.getIndeterminateDrawable().getBounds();
                drawable.setBounds(rect);
                loading.setIndeterminateDrawable(drawable);
            }
        }
        if (noDataRes > 0) {
            noData.setBackgroundResource(noDataRes);
        }
        if (noNetworkRes > 0) {
            noNetwork.setBackgroundResource(noNetworkRes);
        }
    }

    /**
     * just call setMode after this View got,
     *
     * @param mode      the current display mode you need;
     * @param showOnAct is showing on content? or hide content?
     * @param hint      show something when it`s change a mode;
     */
    public void setMode(DisplayMode mode, String hint, boolean showOnAct) {
        if (mode == DisplayMode.none) mode = DisplayMode.normal;
        int hashCode = (showOnAct ? -10 : 10) + mode.value;
        int curCode = (showOnAct ? -10 : 10) + oldMode.value;
        oldMode = mode;
        boolean isSameMode = hashCode == curCode;

        String hintText = (!TextUtils.isEmpty(hint) ? hint : getHintString(mode));
        if (hint != null) {
            tvHint.setText(hint);
        }
        refreshEnableWithView = refreshEnable && (mode == DisplayMode.noData || mode == DisplayMode.noNetwork);
        tvRefresh.setVisibility(refreshEnableWithView ? View.VISIBLE : View.GONE);
        if (valueAnimator == null) {
            valueAnimator = new BaseLoadingValueAnimator(listener);
            valueAnimator.setDuration(defaultAnimationDuration);
        } else {
            valueAnimator.end();
        }
        disPlayViews.put(mode, 0.0f);
        //背景样式改变或mode改变，需要重绘背景或样式
        if (!isSameMode) {
            needBackgroundColor = showOnAct ? bgColorOnAct : bgColor;
            valueAnimator.start(mode, showOnAct);
        }
    }

    private String getHintString(DisplayMode mode) {
        switch (mode) {
            case loading:
                return getContext().getString(R.string.loading_progress);
            case noData:
                return getContext().getString(R.string.loading_no_data);
            case noNetwork:
                return getContext().getString(R.string.loading_no_network);
            default:
                return "";
        }
    }

    /**
     * just call setMode after this View got,
     *
     * @param mode      the current display mode you need;
     * @param showOnAct is showing on content? or hide content?
     * @param hint      show something when it`s change a mode;
     */
    public void setMode(DisplayMode mode, String hint, boolean showOnAct, int delayDismissTime) {
        setMode(mode, hint, showOnAct);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setMode(DisplayMode.normal, "", false);
            }
        }, delayDismissTime);
    }


    private synchronized void onAnimationFraction(float duration, float offset, DisplayMode curMode) {
        setViews(offset, curMode);
        setBackground(duration, offset, curMode);
    }


    private void setViews(float offset, DisplayMode curMode) {
        for (Map.Entry<DisplayMode, Float> entry : disPlayViews.entrySet()) {
            View curSetView = getDisplayView(entry.getKey());
            if (curSetView != null) {
                float curAlpha = entry.getValue();
                float newAlpha;
                if (entry.getKey() == curMode) {
                    //need show
                    if (curSetView.getVisibility() != VISIBLE) {
                        curSetView.setVisibility(VISIBLE);
                        curSetView.setAlpha(0);
                    }
                    newAlpha = Math.min(1.0f, Math.max(0.0f, curAlpha) + offset);
                    curSetView.setAlpha(newAlpha);
                } else {
                    //need hide
                    newAlpha = Math.max(Math.min(1.0f, curAlpha) - offset, 0);
                    curSetView.setAlpha(newAlpha);
                    if (newAlpha == 0 && curSetView.getVisibility() != GONE)
                        curSetView.setVisibility(GONE);
                }
                disPlayViews.put(entry.getKey(), newAlpha);
            }
        }
    }

    private void setBackground(float duration, float offset, DisplayMode curMode) {
        if (curMode != DisplayMode.normal) {
            //画背景
            if (getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
                setAlpha(0);
            }
            if (getAlpha() >= 1.0f) {
                if (oldBackgroundColor != needBackgroundColor) {
                    int curBackgroundColor = (int) argbEvaluator.evaluate(duration, oldBackgroundColor, needBackgroundColor);
                    oldBackgroundColor = curBackgroundColor;
                    setBackgroundColor(curBackgroundColor);
                }
            } else {
                setAlpha(Math.min(1.0f, duration));
                if (oldBackgroundColor != needBackgroundColor) {
                    setBackgroundColor(needBackgroundColor);
                    oldBackgroundColor = needBackgroundColor;
                }
            }
        } else {
            setAlpha(1.0f - duration);
            if (getAlpha() <= 0.05f) {
                setAlpha(0);
                setBackgroundColor(oldBackgroundColor = 0);
                setVisibility(GONE);
            }
        }
    }

    private View getDisplayView(DisplayMode mode) {
        switch (mode) {
            case noData:
                return noData;
            case loading:
                return loading;
            case noNetwork:
                return noNetwork;
        }
        return null;
    }

    private <T extends View> T f(int id) {
        return (T) rootView.findViewById(id);
    }


    public static class BaseLoadingValueAnimator extends ValueAnimator {

        private DisplayMode curMode;
        private boolean isShowOnAct;
        private float curDuration;
        private boolean isCancel;

        private BaseLoadingAnimatorListener listener;

        private void start(DisplayMode mode, boolean isShowOnAct) {
            if (isRunning()) cancel();
            this.curMode = mode;
            this.isShowOnAct = isShowOnAct;
            super.start();
        }

        @Override
        public void cancel() {
            removeAllListeners();
            if (listener != null) listener = null;
            isCancel = true;
            super.cancel();
        }

        private BaseLoadingValueAnimator(BaseLoadingAnimatorListener l) {
            this.listener = l;
            setFloatValues(0.0f, 1.0f);
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
                        listener.onAnimEnd(animation, curMode, isShowOnAct);
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
                        listener.onDurationChange(animation, offset, curMode, isShowOnAct);
                        curDuration = duration;
                    }
                }
            });
        }

        public void setAnimatorListener(BaseLoadingAnimatorListener listener) {
            this.listener = listener;
        }
    }

    public interface BaseLoadingAnimatorListener {

        void onDurationChange(ValueAnimator animation, float duration, DisplayMode mode, boolean isShowOnAct);

        void onAnimEnd(Animator animation, DisplayMode mode, boolean isShowOnAct);
    }
}
