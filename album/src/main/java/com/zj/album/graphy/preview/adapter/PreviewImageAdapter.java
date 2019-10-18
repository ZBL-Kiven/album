package com.zj.album.graphy.preview.adapter;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.album.R;
import com.zj.album.graphy.preview.listener.FullPreviewListener;
import com.zj.album.imageloader.impl.GlideLoader;
import com.zj.album.imageloader.utils.ImageEvaluate;
import com.zj.album.widget.image.ImageViewTouch;
import com.zj.album.widget.image.ImageViewTouchBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangji
 */
public class PreviewImageAdapter extends PagerAdapter implements ImageViewTouch.OnImageViewTouchSingleTapListener {


    private List<ImageViewTouch> imageViews = new ArrayList<>();
    private final FullPreviewListener listener;
    private List<String> paths;

    public PreviewImageAdapter(FullPreviewListener listener) {
        this.listener = listener;
    }


    public void setItems(List<String> path) {
        this.paths = path;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.item_preview3, container, false);
        container.addView(view);

        ImageViewTouch ivt = view.findViewById(R.id.imageViewTouch);
        String url = paths.get(position);
        Point size = ImageEvaluate.getBitmapSize(url);
        ivt.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        ivt.resetMatrix();
        new GlideLoader().loadImage(ivt, size.x, size.y, url);
        ivt.setSingleTapListener(this);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onSingleTapConfirmed() {
        if (listener != null) {
            listener.onFull();
        }
    }
}
