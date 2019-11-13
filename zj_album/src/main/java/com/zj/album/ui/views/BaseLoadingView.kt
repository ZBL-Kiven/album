@file:Suppress("unused")

package com.zj.album.ui.views

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.zj.album.R
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author ZJJ on 2018/7/3.
 */
internal class BaseLoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var oldMode = DisplayMode.NONE
    private var disPlayViews: MutableMap<DisplayMode, Float>? = null
    private var contentView: View? = null
    private var vLoading: ProgressBar? = null
    private var vNoData: View? = null
    private var vNoNetWork: View? = null
    private var blvChildBg: View? = null
    private var curBackgroundView: View? = null
    private var tvHint: TextView? = null
    private var tvRefresh: TextView? = null
    private var refresh: (() -> Unit)? = null

    private var bgColor: Int = 0
    private var bgColorOnAct: Int = 0
    private var needBackgroundColor: Int = 0
    private var oldBackgroundColor: Int = 0
    private var noDataRes = -1
    private var noNetworkRes = -1
    private var loadingRes = -1
    private var hintTextColor: Int = 0
    private var refreshTextColor: Int = 0

    private var showOnActDefault: Boolean = false

    private var loadingHint: String = ""
    private var noDataHint: String = ""
    private var networkErrorHint: String = ""
    private var refreshHint: String? = ""

    private var argbEvaluator: ArgbEvaluator? = null
    private var refreshEnable = true
    private var refreshEnableWithView = false

    private var valueAnimator: BaseLoadingValueAnimator? = null

    private val listener = object : BaseLoadingAnimatorListener {

        override fun onDurationChange(animation: ValueAnimator, duration: Float, mode: DisplayMode?, isShowOnAct: Boolean) {
            synchronized(this@BaseLoadingView) {
                onAnimationFraction(animation.animatedFraction, duration, mode)
            }
        }

        override fun onAnimEnd(animation: Animator, mode: DisplayMode?, isShowOnAct: Boolean) {
            synchronized(this@BaseLoadingView) {
                onAnimationFraction(1.0f, 1.0f, mode)
            }
        }
    }

    init {
        init(context, attrs)
    }

    fun setRefreshEnable(enable: Boolean) {
        this.refreshEnable = enable
    }

    enum class DisplayMode(internal val value: Int) {
        NONE(0), LOADING(1), NO_DATA(2), NET_ERROR(3), DISMISS(4)
    }

    /**
     * when you set mode as NO_DATA/NET_ERROR ,
     * you can get the event when this view was clicked
     * and you can refresh content  when the  "onCallRefresh()" callback
     */
    fun setRefreshListener(refresh: () -> Unit) {
        this.refresh = refresh
        setOnClickListener {
            if (refreshEnable && refreshEnableWithView && this@BaseLoadingView.refresh != null) {
                this@BaseLoadingView.refresh?.invoke()
            }
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.BaseLoadingView)
            try {
                bgColor = array.getColor(R.styleable.BaseLoadingView_backgroundFill, ContextCompat.getColor(context, R.color.pg_color_loading_color_background))
                bgColorOnAct = array.getColor(R.styleable.BaseLoadingView_backgroundOnAct, ContextCompat.getColor(context, R.color.pg_color_loading_background_float))
                noDataRes = array.getResourceId(R.styleable.BaseLoadingView_noDataRes, -1)
                noNetworkRes = array.getResourceId(R.styleable.BaseLoadingView_noNetworkRes, -1)
                loadingRes = array.getResourceId(R.styleable.BaseLoadingView_loadingRes, -1)
                hintTextColor = array.getColor(R.styleable.BaseLoadingView_hintColor, -1)
                refreshTextColor = array.getColor(R.styleable.BaseLoadingView_refreshTextColor, -1)
                loadingHint = array.getString(R.styleable.BaseLoadingView_loadingText) ?: ""
                noDataHint = array.getString(R.styleable.BaseLoadingView_noDataText) ?: ""
                networkErrorHint = array.getString(R.styleable.BaseLoadingView_networkErrorText) ?: ""
                refreshHint = array.getString(R.styleable.BaseLoadingView_refreshText)
                showOnActDefault = array.getBoolean(R.styleable.BaseLoadingView_showOnActDefault, false)
                refreshEnable = array.getBoolean(R.styleable.BaseLoadingView_refreshEnable, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                array.recycle()
            }
        }
        initView(context)
    }

    private fun initView(context: Context) {
        contentView = View.inflate(context, R.layout.loading_view, this)
        vNoData = f(R.id.blv_vNoData)
        vNoNetWork = f(R.id.blv_vNoNetwork)
        vLoading = f(R.id.blv_pb)
        tvHint = f(R.id.blv_tvHint)
        tvRefresh = f(R.id.blv_tvRefresh)
        blvChildBg = f(R.id.blv_child_bg)
        if (refreshHint != null && !refreshHint.isNullOrEmpty()) tvRefresh?.text = refreshHint
        if (hintTextColor != 0) tvHint?.setTextColor(hintTextColor)
        if (refreshTextColor != 0) tvRefresh?.setTextColor(refreshTextColor)
        argbEvaluator = ArgbEvaluator()
        disPlayViews = EnumMap(com.zj.album.ui.views.BaseLoadingView.DisplayMode::class.java)
        disPlayViews?.put(DisplayMode.LOADING, 0.0f)
        tvHint?.text = loadingHint
        resetUi()
        resetBackground(showOnActDefault)
    }

    private fun resetBackground(showOnAct: Boolean) {
        curBackgroundView = if (showOnAct) blvChildBg else this
        blvChildBg?.setBackgroundColor(if (showOnAct) bgColorOnAct else 0)
        setBackgroundColor(if (showOnAct) 0 else bgColor)
    }

    /**
     * @param drawableRes must be an animatorDrawable in progressBar;
     * @link call resetUi() after set this
     */
    fun setLoadingDrawable(drawableRes: Int): BaseLoadingView {
        this.loadingRes = drawableRes
        return this
    }

    //call resetUi() after set this
    fun setNoDataDrawable(drawableRes: Int): BaseLoadingView {
        this.noDataRes = drawableRes
        return this
    }

    //call resetUi() after set this
    fun setNetErrorDrawable(drawableRes: Int): BaseLoadingView {
        this.noNetworkRes = drawableRes
        return this
    }

    //reset LOADING/NO_DATA/NET_ERROR drawable
    private fun resetUi() {
        if (loadingRes > 0) {
            val drawable = context.getDrawable(loadingRes)
            if (drawable != null) {
                val rect = vLoading?.indeterminateDrawable?.bounds
                if (rect != null) drawable.bounds = rect
                vLoading?.indeterminateDrawable = drawable
            }
        }
        if (noDataRes > 0) {
            vNoData?.setBackgroundResource(noDataRes)
        }
        if (noNetworkRes > 0) {
            vNoNetWork?.setBackgroundResource(noNetworkRes)
        }
    }

    /**
     * just call setMode after this View got,
     *
     * @param m      the current display mode you need;
     * @param showOnContent is showing on content? or hide content?
     * @param hint      show something when it`s change a mode;
     */
    @JvmOverloads
    fun setMode(m: DisplayMode, hint: String = "", showOnContent: Boolean? = null) {
        var mode = m
        var showOnAct = showOnContent
        if (showOnAct == null) showOnAct = showOnActDefault
        if (mode == DisplayMode.NONE) mode = DisplayMode.DISMISS
        val newCode = (if (showOnAct) -10 else 10) + mode.value
        val oldCode = (if (showOnAct) -10 else 10) + oldMode.value
        oldMode = mode
        val isSameMode = newCode == oldCode
        val hintText = if (!TextUtils.isEmpty(hint)) hint else getHintString(mode)
        if (hintText.isNotEmpty()) {
            tvHint?.text = hintText
        }
        refreshEnableWithView = refreshEnable && (mode == DisplayMode.NO_DATA || mode == DisplayMode.NET_ERROR)
        tvRefresh?.visibility = if (refreshEnableWithView) View.VISIBLE else View.INVISIBLE
        if (valueAnimator == null) {
            valueAnimator = BaseLoadingValueAnimator(listener)
            valueAnimator?.duration = DEFAULT_ANIM_DURATION
        } else {
            valueAnimator?.end()
        }
        disPlayViews?.put(mode, 0.0f)
        if (!isSameMode) {
            resetBackground(showOnAct)
            needBackgroundColor = if (showOnAct) bgColorOnAct else bgColor
            valueAnimator?.start(mode, showOnAct)
        }
    }

    private fun getHintString(mode: DisplayMode): String {
        return when (mode) {
            DisplayMode.LOADING -> if (loadingHint.isEmpty()) "LOADING" else loadingHint
            DisplayMode.NO_DATA -> if (noDataHint.isEmpty()) "no data found" else noDataHint
            DisplayMode.NET_ERROR -> if (networkErrorHint.isEmpty()) "no network access" else networkErrorHint
            else -> ""
        }
    }

    fun hideDelay(delayDismissTime: Int) {
        postDelayed({ setMode(DisplayMode.DISMISS, "", false) }, delayDismissTime.toLong())
    }


    @Synchronized
    private fun onAnimationFraction(duration: Float, offset: Float, curMode: DisplayMode?) {
        setViews(offset, curMode)
        setBackground(duration, curMode)
    }


    private fun setViews(offset: Float, curMode: DisplayMode?) {
        disPlayViews?.forEach { (key, curAlpha) ->
            val curSetView = getDisplayView(key)
            if (curSetView != null) {
                val newAlpha: Float
                if (key == curMode) {
                    //need show
                    if (curSetView.visibility != View.VISIBLE) {
                        curSetView.visibility = View.VISIBLE
                        curSetView.alpha = 0f
                    }
                    newAlpha = min(1.0f, max(0.0f, curAlpha) + offset)
                    curSetView.alpha = newAlpha
                } else {
                    //need hide
                    newAlpha = max(min(1.0f, curAlpha) - offset, 0f)
                    curSetView.alpha = newAlpha
                    if (newAlpha == 0f && curSetView.visibility != View.GONE) curSetView.visibility = View.GONE
                }
                disPlayViews?.put(key, newAlpha)
            }
        }
    }

    private fun setBackground(duration: Float, curMode: DisplayMode?) {
        if (curMode != DisplayMode.DISMISS) {
            if (visibility != View.VISIBLE) {
                alpha = 0f
                visibility = View.VISIBLE
            }
            if (alpha >= 1.0f) {
                if (oldBackgroundColor != needBackgroundColor) {
                    curBackgroundView?.setBackgroundColor(needBackgroundColor)
                    oldBackgroundColor = needBackgroundColor
                }
            } else {
                alpha = min(1.0f, duration)
                if (oldBackgroundColor != needBackgroundColor) {
                    val curBackgroundColor = argbEvaluator?.evaluate(duration, oldBackgroundColor, needBackgroundColor) as Int
                    oldBackgroundColor = curBackgroundColor
                    curBackgroundView?.setBackgroundColor(curBackgroundColor)
                }
            }
        } else {
            alpha = 1.0f - duration
            if (alpha <= 0.05f) {
                alpha = 0f
                setBackgroundColor({ oldBackgroundColor = 0;oldBackgroundColor }.invoke())
                visibility = View.GONE
            }
        }
    }

    private fun getDisplayView(mode: DisplayMode): View? {
        return when (mode) {
            DisplayMode.NO_DATA -> vNoData
            DisplayMode.LOADING -> vLoading
            DisplayMode.NET_ERROR -> vNoNetWork
            else -> null
        }
    }

    private fun <T : View> f(id: Int): T? {
        return contentView?.findViewById<T>(id)
    }

    private class BaseLoadingValueAnimator constructor(private var listener: BaseLoadingAnimatorListener?) : ValueAnimator() {

        private var curMode: DisplayMode? = null
        private var isShowOnAct: Boolean = false
        private var curDuration: Float = 0.toFloat()
        private var isCancel: Boolean = false

        fun start(mode: DisplayMode, isShowOnAct: Boolean) {
            if (isRunning) cancel()
            this.curMode = mode
            this.isShowOnAct = isShowOnAct
            super.start()
        }

        override fun cancel() {
            removeAllListeners()
            if (listener != null) listener = null
            isCancel = true
            super.cancel()
        }

        init {
            setFloatValues(0.0f, 1.0f)
            addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    if (curDuration != 0f) curDuration = 0f
                }

                override fun onAnimationEnd(animation: Animator) {
                    curDuration = 0f
                    if (isCancel) return
                    if (listener != null) listener?.onAnimEnd(animation, curMode, isShowOnAct)
                }

                override fun onAnimationCancel(animation: Animator) {
                    curDuration = 0f
                }

                override fun onAnimationRepeat(animation: Animator) {
                    curDuration = 0f
                }
            })

            addUpdateListener(AnimatorUpdateListener { animation ->
                if (isCancel) return@AnimatorUpdateListener
                if (listener != null) {
                    val duration = animation.animatedValue as Float
                    val offset = duration - curDuration
                    listener?.onDurationChange(animation, offset, curMode, isShowOnAct)
                    curDuration = duration
                }
            })
        }

        fun setAnimatorListener(listener: BaseLoadingAnimatorListener) {
            this.listener = listener
        }
    }

    interface BaseLoadingAnimatorListener {

        fun onDurationChange(animation: ValueAnimator, duration: Float, mode: DisplayMode?, isShowOnAct: Boolean)

        fun onAnimEnd(animation: Animator, mode: DisplayMode?, isShowOnAct: Boolean)
    }

    companion object {

        private const val DEFAULT_ANIM_DURATION = 400L
    }
}
