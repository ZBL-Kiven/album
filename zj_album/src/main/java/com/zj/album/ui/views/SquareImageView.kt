package com.zj.album.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec))
        val childWidthSize = measuredWidth
        val w = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
        super.onMeasure(w, w)
    }
}