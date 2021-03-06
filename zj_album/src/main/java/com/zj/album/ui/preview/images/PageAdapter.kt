package com.zj.album.ui.preview.images

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup
import com.zj.album.nutils.getPointIndexItem
import kotlin.math.min

/**
 * @author ZJJ on 2019.10.24
 * */
internal class PageAdapter<T : Any?>(private val resId: Int, var context: Context?, private var adapter: OnPageChange<T>?) : PagerAdapter(), ViewPager.OnPageChangeListener {

    private var isFirst = true
    private var oldPosition: Int = -1
    private var curPosition: Int = -1
    private var curViewPosition: Int = 0
    private var displayChange: Int = 0
    private var initPosition: Int = 0
    private var curSelectedPosition: Int = 0
    private var mData: List<T>? = null
    private val maxCount: Int

    //const value
    private val maxGcSize = 3

    init {
        val halfMax = (Int.MAX_VALUE / 2f).toInt()
        val realMaxOffset = min(1, halfMax % maxGcSize)
        maxCount = (halfMax - realMaxOffset) * 2 + 1
    }

    fun setData(data: List<T>?, curItem: Int) {
        isFirst = true
        mData = data
        curSelectedPosition = curItem
        initPosition = curItem
        displayChange = curItem

        fun getView(tag: Int): View {
            val v = View.inflate(context, resId, null)
            v.tag = tag
            return v
        }
        views = mutableListOf<View>().apply {
            mData?.let {
                if (it.isEmpty()) return@let
                if (it.size <= 1) add(getView(0))
                else for (i in 0 until maxGcSize) {
                    add(getView(i))
                }
            }
        }
    }

    private var views = mutableListOf<View>()

    override fun getCount(): Int {
        return if (views.size == 1) views.size else maxCount
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view.tag === obj
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (views.isNullOrEmpty()) return 0
        val viewPosition = position % views.size
        val view = views[viewPosition]
        if (container == view.parent) {
            container.removeView(view)
        }
        fillItem(view, viewPosition)
        container.addView(view, ViewPager.LayoutParams())
        return view.tag
    }

    private fun fillItem(v: View, position: Int) {
        if (isFirst) {
            mData?.let { data ->
                if (adapter != null) {
                    var index = 0
                    if (position != 0) index = if (position == views.lastIndex) -1 else 2
                    curSelectedPosition += index
                    if (curSelectedPosition < 0) curSelectedPosition = data.size - 1
                    if (curSelectedPosition >= data.size) curSelectedPosition -= data.size
                    adapter?.onBindData(data[curSelectedPosition], v)
                }
                if (position == 1) {
                    isFirst = false
                    curSelectedPosition = calculatePositionWithRange(curSelectedPosition - 1, data.size)
                }
            }
        }
    }

    private fun onPageScrolled(position: Int) {
        mData?.let { data ->
            curPosition = position % views.size
            if (oldPosition == -1) oldPosition = curPosition
            if (isFirst || oldPosition == curPosition) {
                return
            }
            val isRightOver = curPosition == 0 && oldPosition == views.lastIndex
            val isLeftOver = curPosition == views.lastIndex && oldPosition == 0
            val isLeft = (curPosition > oldPosition || isRightOver) && !isLeftOver
            oldPosition = curPosition
            var nextDisplayPosition: Int
            var nextViewPosition: Int

            if (isLeft) {
                curSelectedPosition++
                curViewPosition++
                nextDisplayPosition = curSelectedPosition + 1
                nextViewPosition = curViewPosition + 1
            } else {
                curSelectedPosition--
                curViewPosition--
                nextDisplayPosition = curSelectedPosition - 1
                nextViewPosition = curViewPosition - 1
            }
            curSelectedPosition = calculatePositionWithRange(curSelectedPosition, data.size)
            nextDisplayPosition = calculatePositionWithRange(nextDisplayPosition, data.size)
            curViewPosition = calculatePositionWithRange(curViewPosition, views.size)
            nextViewPosition = calculatePositionWithRange(nextViewPosition, views.size)
            adapter?.onBindData(data[nextDisplayPosition], views[nextViewPosition])
        }
    }

    private fun calculatePositionWithRange(position: Int, max: Int): Int {
        if (position >= max) {
            return position - max
        }
        if (position < 0) {
            return position + max
        }
        return position
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
    }


    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        if (p1 > 0.0f) handlerScrollState(p0)
        if (p1 == 0.0f) handlerCurItemChanged(p0)
        when (p1) {
            0.0f -> {
                adapter?.onScrollStateChanged(p1, ViewPager.SCROLL_STATE_IDLE)
            }
            1.0f -> {
                adapter?.onScrollStateChanged(p1, ViewPager.SCROLL_STATE_SETTLING)
            }
            else -> {
                adapter?.onScrollStateChanged(p1, ViewPager.SCROLL_STATE_DRAGGING)
            }
        }

    }

    override fun onPageSelected(p0: Int) {
        onPageScrolled(p0)
    }

    private var triggerScrollPosition = -1
    private fun handlerScrollState(position: Int) {
        if (triggerScrollPosition == position) return
        triggerItemPosition = -1
        triggerScrollPosition = position
        onFocusChanged(false)
    }

    private var triggerItemPosition = -1
    private fun handlerCurItemChanged(position: Int) {
        if (triggerItemPosition == position) return
        triggerScrollPosition = -1
        triggerItemPosition = position
        onFocusChanged(true)
    }

    private fun onFocusChanged(focus: Boolean) {
        adapter?.onFocusChange(getPointIndexItem(views, curViewPosition), getPointIndexItem(mData, curSelectedPosition), focus)
    }

    fun clear() {
        if (curViewPosition in 0 until views.size && curSelectedPosition in 0 until (mData?.size ?: 0)) {
            onFocusChanged(false)
        }
        adapter = null
        context = null
        mData = null
        fun removeViews(vg: ViewGroup) {
            if (vg.childCount <= 0) return
            val views = mutableListOf<View>()
            for (i in 0 until vg.childCount) {
                val v = vg.getChildAt(i)
                views.add(v)
            }
            views.forEach {
                vg.removeView(it)
                if (it is ViewGroup) removeViews(it)
            }
        }
        views.forEach { v ->
            (v as? ViewGroup)?.let { removeViews(it) }
        }
        views.clear()
    }
}