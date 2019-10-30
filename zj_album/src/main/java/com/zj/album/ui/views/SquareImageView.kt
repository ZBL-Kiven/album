package com.zj.album.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import com.zj.album.R

/**
 * @author ZJJ on 2019.10.24
 * */
internal class SquareImageView : ImageView {

    private var orientation: Int = 0
    private var ratio: Float = 1.0f

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        if (attrs != null) {
            var ta: TypedArray? = null
            try {
                ta = context.obtainStyledAttributes(attrs, R.styleable.SquareImageView)
            } finally {
                ta?.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec))
        val childSize = if (orientation == 0) measuredWidth else measuredHeight
        val size = MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY)
        var width = size
        var height = size
        if (orientation == 0) height = (height * ratio).toInt()
        else width = (width * ratio).toInt()
        super.onMeasure(width, height)
    }
}