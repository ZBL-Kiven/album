package com.zj.album.ui.preview.images

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.zj.album.nutils.log
import kotlin.math.max

internal class PageAdapter<T : Any?>(val context: Context, private val maxGcSize: Int, private val adapter: OnPageChange<T>?) : PagerAdapter(), ViewPager.OnPageChangeListener {

    private var isFirst = true
    private var oldPosition: Int = 0
    private var curPosition: Int = 0
    private var curNextPosition: Int = 0
    private var displayChange: Int = 0
    private var initPosition: Int = 0
    private var curSelectedPosition: Int = 0
    private var curOrientation: TouchOrientation? = null

    private var mData: List<T>? = null

    fun setData(resId: Int, data: List<T>?, curItem: Int) {
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

    private var views = listOf<View>()

    override fun getCount(): Int {
        return if (views.size <= 1) views.size else 100
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view.tag === obj
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (views.isNullOrEmpty()) return 0
        val viewPosition = getViewPosition(position)
        val view = views[viewPosition]
        if (container == view.parent) {
            container.removeView(view)
        }
        if (!isFirst) curNextPosition = viewPosition
        log("v = $viewPosition     p =  $position ")
        fillBannerItem(view, position)
        container.addView(view, ViewPager.LayoutParams())
        return view.tag
    }

    private fun getViewPosition(position: Int): Int {
        return position % max(views.size, 1)
    }

    private var offset = 0
    private fun fillBannerItem(v: View, dataPosition: Int) {
        if (isFirst) {
            var position = dataPosition
            if (adapter != null) {
                mData?.let { data ->
                    if (offset == 0) {
                        offset = position % maxGcSize
                    }
                    position -= offset
                    position %= maxGcSize
                    var index = 0
                    if (position != 0) index = if (position == maxGcSize - 1) -1 else 1
                    var curIndex = curSelectedPosition
                    curIndex += index
                    if (curIndex < 0) curIndex = data.size - 1
                    if (curIndex >= data.size) curIndex -= data.size
                    adapter.onChange(data[curIndex], v)
                }
            }
            if (position == 1) {
                isFirst = false
                offset = 0
            }
        }
    }

    private fun loadNextData(position: Int) {
        if (isFirst) {
            return
        }
        mData?.let { data ->
            curPosition = position
            //when it selected 0，the max value was next ，initialized the last data of source，but displaying data is the previous of next
            if (maxGcSize == data.size) curSelectedPosition = curPosition
            val isRightOver = curPosition == maxGcSize - 1 && oldPosition == 0
            val isLeftOver = curPosition == 0 && oldPosition == maxGcSize - 1
            // @param curOrientation invalidate after there
            // @param curSelectedPosition invalidate after there
            if (curPosition != oldPosition) {
                curOrientation = if ((curPosition > oldPosition || isLeftOver) && !isRightOver) {
                    //sliding to left , to load next
                    TouchOrientation.LEFT
                } else {
                    //sliding to right , to load previous
                    TouchOrientation.RIGHT
                }
                curSelectedPosition = if (curOrientation == TouchOrientation.LEFT) {
                    displayChange++
                    //the data should showing to next ，and the next is loaded yet.
                    displayChange + 1
                } else {
                    displayChange--
                    //the data should showing to previous, but the previous was this self
                    displayChange - 1
                }
                //if the next data index is over than value of constrain
                if (curSelectedPosition >= data.size) {
                    curSelectedPosition -= data.size
                }
                if (curSelectedPosition < 0) {
                    curSelectedPosition += data.size
                }
                if (displayChange >= data.size) displayChange = 0
                if (displayChange < 0) displayChange = data.size - 1
            }
            // loading the next of index，may at previous or next of current
            if (adapter != null) {
                adapter.onChange(data[curSelectedPosition], views[curNextPosition])
                adapter.onDisplayChange(displayChange)
            }
            //the data reduced ,position is invalidate form this after
            log("old  = $oldPosition     cur =  $curPosition ")
            oldPosition = curPosition
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(views[getViewPosition(position)])
    }

    override fun onPageScrollStateChanged(p0: Int) {
        when (p0) {
            ViewPager.SCROLL_STATE_IDLE -> {
                isHandlerScrollState = false
            }
        }
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        handlerScrollState(getViewPosition(p0))
    }

    override fun onPageSelected(p0: Int) {
        loadNextData(getViewPosition(p0))
    }

    private var isHandlerScrollState = false

    private fun handlerScrollState(position: Int) {
        if (isHandlerScrollState) return
        isHandlerScrollState = true
        adapter?.onFocusChange(views[position], mData?.get(curSelectedPosition))
    }
}