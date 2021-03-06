package com.zj.album.ui.preview.images

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import com.zj.album.ui.preview.images.transformer.TransitionEffect

/**
 * @author ZJJ on 2019.10.24
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class BannerViewPager<T : Any?> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    private var mAllowUserScrollable: () -> Boolean = { true }
    private var pageAdapter: PageAdapter<T>? = null

    fun init(resId: Int, overScrollMode: Int, mTransitionEffect: TransitionEffect, onPageChange: OnPageChange<T>) {
        pageAdapter?.let {
            it.clear()
            it.notifyDataSetChanged()
        }
        removeAllViews()
        PageAdapter(resId, context, onPageChange).let {
            pageAdapter = it
            adapter = it
            addOnPageChangeListener(it)
        }
        offscreenPageLimit = 1
        setOverScrollMode(overScrollMode)
        setPageTransformer(true, mTransitionEffect.effect)
    }

    fun setData(data: List<T>?, curItem: Int) {
        pageAdapter?.setData(data, curItem)
        currentItem = ((adapter?.count) ?: 0) / 2
        pageAdapter?.notifyDataSetChanged()
        if (data.isNullOrEmpty() || data.size == 1) {
            mAllowUserScrollable = { false }
        }
    }

    override fun setPageTransformer(reverseDrawingOrder: Boolean, transformer: PageTransformer?) {
        val viewpagerClass = ViewPager::class.java
        try {
            val hasTransformer = transformer != null
            val pageTransformerField = viewpagerClass.getDeclaredField("mPageTransformer")
            pageTransformerField.isAccessible = true
            pageTransformerField.set(this, transformer)
            val drawingOrderField = viewpagerClass.getDeclaredField("mDrawingOrder")
            drawingOrderField.isAccessible = true
            if (hasTransformer) {
                drawingOrderField.setInt(this, if (reverseDrawingOrder) 2 else 1)
            } else {
                drawingOrderField.setInt(this, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAllowUserScrollable(allowUserScrollable: () -> Boolean) {
        mAllowUserScrollable = allowUserScrollable
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (mAllowUserScrollable()) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    fun setMargin(margin: Int) {
        pageMargin = margin
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (mAllowUserScrollable()) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }
}