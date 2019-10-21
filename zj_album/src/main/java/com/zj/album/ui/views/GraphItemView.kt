package com.zj.album.ui.views

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.zj.album.R

class GraphItemView(context: Context) : FrameLayout(context) {


    init {
        View.inflate(context, R.layout.graph_item_selected, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }


}
