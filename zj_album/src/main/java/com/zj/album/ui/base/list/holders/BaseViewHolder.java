package com.zj.album.ui.base.list.holders;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zj.album.ui.base.list.adapters.BaseRecyclerAdapter;

/**
 * Created by ZJJ on 2018/4/4.
 */

@SuppressWarnings({"unused", "unchecked", "WeakerAccess"})
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> parseArray;
    private BaseRecyclerAdapter mAdapter;

    public BaseViewHolder(BaseRecyclerAdapter adapter, View v) {
        super(v);
        this.mAdapter = adapter;
        parseArray = new SparseArray<>();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.onClickListener != null)
                    mAdapter.onClickListener.onItemClick(getLayoutPosition(), itemView, mAdapter.getItem(getLayoutPosition()));
            }
        });
    }

    public <T extends View> T getView(@IdRes int id) {
        T v = (T) parseArray.get(id);
        if (v == null) {
            v = itemView.findViewById(id);
            parseArray.put(id, v);
        }
        return v;
    }

    public void setText(@IdRes int id, String s) {
        TextView v = getView(id);
        v.setText(s);
    }

    public TextView setTextColor(@IdRes int viewId, @ColorInt int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return view;
    }

    public ImageView setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return view;
    }

    public View setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return view;
    }

    public View setBackgroundRes(@IdRes int viewId, @DrawableRes int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return view;
    }


    public ImageView setImageDrawable(@IdRes int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return view;
    }

    public ImageView setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return view;
    }

}
