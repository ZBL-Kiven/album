package com.zj.album.ui.preview

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * @author ZJJ on 2019.10.24
 */
internal class PreviewFullValueAnimator(private var listener: FullAnimatorListener?) : ValueAnimator() {

    private var isFull: Boolean = false
    private var curDuration: Float = 0.toFloat()
    private var isCancel: Boolean = false

    fun start(isFull: Boolean) {
        if (isRunning) cancel()
        this.isFull = isFull
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
        interpolator = AccelerateDecelerateInterpolator()
        addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (curDuration != 0f) curDuration = 0f
            }

            override fun onAnimationEnd(animation: Animator) {
                curDuration = 0f
                if (isCancel) return
                listener?.onAnimEnd(animation, isFull)
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
                listener?.onDurationChange(animation, offset, isFull)
                curDuration = duration
            }
        })
    }

    interface FullAnimatorListener {

        fun onDurationChange(animation: ValueAnimator, duration: Float, isFull: Boolean)

        fun onAnimEnd(animation: Animator, isFull: Boolean)
    }
}

