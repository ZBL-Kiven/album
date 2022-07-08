package com.zj.album.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import com.zj.album.R

/**
 * @author ZJJ on 2019.10.24
 * */
internal class SquareImageView : androidx.appcompat.widget.AppCompatImageView {

    private var orientation: Int = 0
    private var ratio: Float = 1.0f

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        if (attrs != null) {
            var ta: TypedArray? = null
            try {
                ta = context.obtainStyledAttributes(attrs, R.styleable.SquareImageView)
                orientation = ta.getInt(R.styleable.SquareImageView_squareWith, 0)
                ratio = ta.getFraction(R.styleable.SquareImageView_squareRatio, 1, 2, 1.0f)
            } finally {
                ta?.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec))
        val childSize = if (orientation == 0) measuredWidth else measuredHeight
        var mesW = childSize
        var mesH = childSize
        if (orientation == 0) mesH = (mesH * ratio).toInt()
        else mesW = (mesW * ratio).toInt()
        val mW = MeasureSpec.makeMeasureSpec(mesW, MeasureSpec.EXACTLY)
        val mH = MeasureSpec.makeMeasureSpec(mesH, MeasureSpec.EXACTLY)
        super.onMeasure(mW, mH)
    }
}