package com.zj.album.ui.preview.images

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import com.zj.album.ui.preview.images.transformer.PageTransformer.getPageTransformer
import com.zj.album.ui.preview.images.transformer.TransitionEffect

/**
* @author ZJJ on 2019.10.24
* */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class BannerViewPager<T : Any?> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    private var mAllowUserScrollable: () -> Boolean = { true }
    private var pageAdapter: PageAdapter<T>? = null
    private var isLocked = false

    fun init(overScrollMode: Int, mTransitionEffect: TransitionEffect, onPageChange: OnPageChange<T>) {
        PageAdapter(context, onPageChange).let {
            pageAdapter = it
            adapter = it
            addOnPageChangeListener(it)
        }
        offscreenPageLimit = 1
        setOverScrollMode(overScrollMode)
        setPageTransformer(true, getPageTransformer(mTransitionEffect))
    }

    fun setData(resId: Int, data: List<T>?, curItem: Int) {
        pageAdapter?.setData(resId, data, curItem)
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

    fun lock() {
        isLocked = true
    }

    fun unLock() {
        isLocked = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (mAllowUserScrollable() && !isLocked) {
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