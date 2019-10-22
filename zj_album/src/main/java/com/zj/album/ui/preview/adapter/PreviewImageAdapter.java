package com.zj.album.ui.preview.adapter;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.album.R;
import com.zj.album.imageloader.impl.GlideLoader;
import com.zj.album.imageloader.utils.ImageEvaluate;
import com.zj.album.nModule.FileInfo;
import com.zj.album.ui.preview.listener.FullPreviewListener;
import com.zj.album.widget.image.ImageViewTouch;
import com.zj.album.widget.image.ImageViewTouchBase;

import java.util.List;

/**
 * @author yangji
 */
public class PreviewImageAdapter extends PagerAdapter implements ImageViewTouch.OnImageViewTouchSingleTapListener {


    private LayoutInflater mInflater;
    private SparseArray<View> imageViews = new SparseArray<>();
    private final FullPreviewListener listener;
    private List<FileInfo> paths;
    private final boolean isCache;
    private final int cacheSize;

    public PreviewImageAdapter(FullPreviewListener listener) {
        this(false, listener);
    }

    public PreviewImageAdapter(boolean isCache, FullPreviewListener listener) {
        this(isCache, 3, listener);
    }

    public PreviewImageAdapter(boolean isCache, int cacheSize, FullPreviewListener listener) {
        this.isCache = isCache;
        this.listener = listener;
        this.cacheSize = cacheSize;
    }

    public void setItems(List<FileInfo> path) {
        this.paths = path;
    }

    @Override
    public int getCount() {
        return paths == null ? 0 : paths.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return paths.indexOf(object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mInflater = mInflater != null ? mInflater : LayoutInflater.from(container.getContext());
        int cachePosition = getCachePosition(position);
        View view = getItemView(cachePosition);
        if (view == null) {
            view = mInflater.inflate(R.layout.album_item_preview_full, container, false);
            imageViews.put(cachePosition, view);
        } else {
            container.removeView(view);
        }
        container.addView(view);
        ImageViewTouch ivt = view.findViewById(R.id.imageViewTouch);
        String url = paths.get(position).getPath();
        Point size = ImageEvaluate.getBitmapSize(url);
        ivt.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        ivt.resetMatrix();
        new GlideLoader().loadImage(ivt, size.x, size.y, url);
        ivt.setSingleTapListener(this);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (!isCache) {
            container.removeView((View) object);
            imageViews.remove(position);
        }
    }

    @Override
    public void onSingleTapConfirmed() {
        if (listener != null) {
            listener.onFull();
        }
    }

    public View getItemView(int position) {
        return imageViews.get(getCachePosition(position));
    }

    private int getCachePosition(int position) {
        return isCache ? position % cacheSize : position;
    }

    public void reset(int position) {
        View view = getItemView(position);
        if (view != null) {
            ImageViewTouch imageViewTouch = view.findViewById(R.id.imageViewTouch);
            if (imageViewTouch != null) {
                imageViewTouch.resetMatrix();
            }
        }
    }

    public FileInfo getItem(int currentItem) {
        return paths.get(currentItem);
    }
}
