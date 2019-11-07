package com.zj.album.ui.views.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.zj.album.ui.views.image.easing.ScaleEffect;
import com.zj.album.ui.views.image.graphics.FastBitmapDrawable;
import com.zj.album.ui.views.image.utils.IDisposable;


/**
 * Base View to manage image zoom/scroll/pinch operations
 */
@SuppressWarnings("unused")
abstract class ImageViewTouchBase extends AppCompatImageView implements IDisposable {

    public interface OnDrawableChangeListener {

        /**
         * Callback invoked when a new drawable has been
         * assigned to the view
         */
        void onDrawableChanged(Drawable drawable);
    }

    public interface OnLayoutChangeListener {
        /**
         * Callback invoked when the layout bounds changed
         */
        void onLayoutChanged(boolean changed, int left, int top, int right, int bottom);
    }

    /**
     * Use this to change the {@link ImageViewTouchBase#setDisplayType(DisplayType)} of
     * this View
     *
     * @author alessandro
     */
    public enum DisplayType {
        /**
         * Image is not scaled by default
         */
        NONE,
        /**
         * Image will be always presented using this view's bounds
         */
        FIT_TO_SCREEN,
        /**
         * Image will be scaled only if bigger than the bounds of this view
         */
        FIT_IF_BIGGER
    }

    public static final String LOG_TAG = "ImageViewTouchBase";
    protected static final boolean LOG_ENABLED = false;

    public static final float ZOOM_INVALID = -1f;

    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    protected Matrix mNextMatrix;
    protected Handler mHandler = new Handler();
    protected Runnable mLayoutRunnable = null;
    protected boolean mUserScaled = false;

    private float mMaxZoom = ZOOM_INVALID;
    private float mMinZoom = ZOOM_INVALID;

    // true when min and max zoom are explicitly defined
    private boolean mMaxZoomDefined;
    private boolean mMinZoomDefined;

    protected final Matrix mDisplayMatrix = new Matrix();
    protected final float[] mMatrixValues = new float[9];

    private int mThisWidth = -1;
    private int mThisHeight = -1;
    private PointF mCenter = new PointF();

    protected DisplayType mScaleType = DisplayType.NONE;
    private boolean mScaleTypeChanged;
    private boolean mBitmapChanged;

    final protected int DEFAULT_ANIMATION_DURATION = 200;

    protected RectF mBitmapRect = new RectF();
    protected RectF mCenterRect = new RectF();
    protected RectF mScrollRect = new RectF();

    private OnDrawableChangeListener mDrawableChangeListener;
    private OnLayoutChangeListener mOnLayoutChangeListener;

    public ImageViewTouchBase(Context context) {
        this(context, null);
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public void setOnDrawableChangedListener(OnDrawableChangeListener listener) {
        mDrawableChangeListener = listener;
    }

    public void setOnLayoutChangeListener(OnLayoutChangeListener listener) {
        mOnLayoutChangeListener = listener;
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        }
    }

    protected ScaleEffect getEasingEffect() {
        return ScaleEffect.CUBIC;
    }

    /**
     * Clear the current drawable
     */
    public void clear() {
        setImageBitmap(null);
    }

    /**
     * Change the display type
     */
    public void setDisplayType(DisplayType type) {
        if (type != mScaleType) {
            mUserScaled = false;
            mScaleType = type;
            mScaleTypeChanged = true;
            requestLayout();
        }
    }

    public DisplayType getDisplayType() {
        return mScaleType;
    }

    protected void setMinScale(float value) {
        mMinZoom = value;
    }

    protected void setMaxScale(float value) {

        mMaxZoom = value;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int deltaX = 0;
        int deltaY = 0;
        if (changed) {
            int oldW = mThisWidth;
            int oldH = mThisHeight;

            mThisWidth = right - left;
            mThisHeight = bottom - top;

            deltaX = mThisWidth - oldW;
            deltaY = mThisHeight - oldH;

            // update center point
            mCenter.x = mThisWidth / 2f;
            mCenter.y = mThisHeight / 2f;
        }

        Runnable r = mLayoutRunnable;

        if (r != null) {
            mLayoutRunnable = null;
            r.run();
        }

        final Drawable drawable = getDrawable();

        if (drawable != null) {

            if (changed || mScaleTypeChanged || mBitmapChanged) {

                float scale = 1;

                // retrieve the old values
                float old_default_scale = getDefaultScale(mScaleType);
                float old_matrix_scale = getScale(mBaseMatrix);
                float old_scale = getScale();
                float old_min_scale = Math.min(1f, 1f / old_matrix_scale);

                getProperBaseMatrix(drawable, mBaseMatrix);

                float new_matrix_scale = getScale(mBaseMatrix);
                // 1. bitmap changed or scale type changed
                if (mBitmapChanged || mScaleTypeChanged) {
                    if (mNextMatrix != null) {
                        mSuppMatrix.set(mNextMatrix);
                        mNextMatrix = null;
                        scale = getScale();
                    } else {
                        mSuppMatrix.reset();
                        scale = getDefaultScale(mScaleType);
                    }

                    setImageMatrix(getImageViewMatrix());

                    if (scale != getScale()) {
                        zoomTo(scale);
                    }
                } else if (changed) {
                    // 2. layout size changed
                    if (!mMinZoomDefined) mMinZoom = ZOOM_INVALID;
                    if (!mMaxZoomDefined) mMaxZoom = ZOOM_INVALID;
                    setImageMatrix(getImageViewMatrix());
                    postTranslate(-deltaX, -deltaY);
                    if (!mUserScaled) {
                        scale = getDefaultScale(mScaleType);
                        zoomTo(scale);
                    } else {
                        if (Math.abs(old_scale - old_min_scale) > 0.001) {
                            scale = (old_matrix_scale / new_matrix_scale) * old_scale;
                        }
                        zoomTo(scale);
                    }
                }
                mUserScaled = false;
                if (scale > getMaxScale() || scale < getMinScale()) {
                    // if current scale if outside the min/max bounds
                    // then restore the correct scale
                    zoomTo(scale);
                }
                center(true, true);
                if (mBitmapChanged) onDrawableChanged(drawable);
                if (changed || mBitmapChanged || mScaleTypeChanged) onLayoutChanged(left, top, right, bottom);
                if (mScaleTypeChanged) mScaleTypeChanged = false;
                if (mBitmapChanged) mBitmapChanged = false;
            }
        } else {
            // drawable is null
            if (mBitmapChanged) onDrawableChanged(null);
            if (changed || mBitmapChanged || mScaleTypeChanged) onLayoutChanged(left, top, right, bottom);

            if (mBitmapChanged) mBitmapChanged = false;
            if (mScaleTypeChanged) mScaleTypeChanged = false;

        }
    }

    /**
     * Restore the original display
     */
    public void resetDisplay() {
        mBitmapChanged = true;
        requestLayout();
    }

    public void resetMatrix() {
        mSuppMatrix = new Matrix();

        float scale = getDefaultScale(mScaleType);
        setImageMatrix(getImageViewMatrix());
        if (scale != getScale()) {
            zoomTo(scale);
        }
        postInvalidate();
    }

    protected float getDefaultScale(DisplayType type) {
        if (type == DisplayType.FIT_TO_SCREEN) {
            // always fit to screen
            return 1f;
        } else if (type == DisplayType.FIT_IF_BIGGER) {
            // normal scale if smaller, fit to screen otherwise
            return Math.min(1f, 1f / getScale(mBaseMatrix));
        } else {
            // no scale
            return 1f / getScale(mBaseMatrix);
        }
    }

    @Override
    public void setImageResource(int resId) {
        setImageDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * {@inheritDoc} Set the new image to display and reset the internal matrix.
     *
     * @param bitmap the {@link Bitmap} to display
     * @see {@link ImageView#setImageBitmap(Bitmap)}
     */
    @Override
    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, null, ZOOM_INVALID, ZOOM_INVALID);
    }

    /**
     * @see #setImageDrawable(Drawable, Matrix, float, float)
     */
    public void setImageBitmap(final Bitmap bitmap, Matrix matrix, float min_zoom, float max_zoom) {
        if (bitmap != null)
            setImageDrawable(new FastBitmapDrawable(bitmap), matrix, min_zoom, max_zoom);
        else
            setImageDrawable(null, matrix, min_zoom, max_zoom);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, null, ZOOM_INVALID, ZOOM_INVALID);
    }

    /**
     * Note: if the scaleType is FitToScreen then min_zoom must be <= 1 and max_zoom must be >= 1
     *
     * @param drawable       the new drawable
     * @param initial_matrix the optional initial display matrix
     * @param min_zoom       the optional minimum scale, pass {@link #ZOOM_INVALID} to use the default min_zoom
     * @param max_zoom       the optional maximum scale, pass {@link #ZOOM_INVALID} to use the default max_zoom
     */
    public void setImageDrawable(final Drawable drawable, final Matrix initial_matrix, final float min_zoom, final float max_zoom) {

        final int viewWidth = getWidth();

        if (viewWidth <= 0) {
            mLayoutRunnable = () -> setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
            return;
        }
        _setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
    }

    protected void _setImageDrawable(final Drawable drawable, final Matrix initial_matrix, float min_zoom, float max_zoom) {
        if (drawable != null) {
            super.setImageDrawable(drawable);
        } else {
            mBaseMatrix.reset();
            super.setImageDrawable(null);
        }

        if (min_zoom != ZOOM_INVALID && max_zoom != ZOOM_INVALID) {
            min_zoom = Math.min(min_zoom, max_zoom);
            max_zoom = Math.max(min_zoom, max_zoom);

            mMinZoom = min_zoom;
            mMaxZoom = max_zoom;

            mMinZoomDefined = true;
            mMaxZoomDefined = true;

            if (mScaleType == DisplayType.FIT_TO_SCREEN || mScaleType == DisplayType.FIT_IF_BIGGER) {

                if (mMinZoom >= 1) {
                    mMinZoomDefined = false;
                    mMinZoom = ZOOM_INVALID;
                }

                if (mMaxZoom <= 1) {
                    mMaxZoom = ZOOM_INVALID;
                }
            }
        } else {
            mMinZoom = ZOOM_INVALID;
            mMaxZoom = ZOOM_INVALID;
            mMinZoomDefined = false;
            mMaxZoomDefined = false;
        }

        if (initial_matrix != null) {
            mNextMatrix = new Matrix(initial_matrix);
        }

        mBitmapChanged = true;
        requestLayout();
    }

    protected void onDrawableChanged(final Drawable drawable) {
        fireOnDrawableChangeListener(drawable);
    }

    protected void fireOnLayoutChangeListener(int left, int top, int right, int bottom) {
        if (null != mOnLayoutChangeListener) {
            mOnLayoutChangeListener.onLayoutChanged(true, left, top, right, bottom);
        }
    }

    protected void fireOnDrawableChangeListener(Drawable drawable) {
        if (null != mDrawableChangeListener) {
            mDrawableChangeListener.onDrawableChanged(drawable);
        }
    }

    /**
     * Called just after {@link #onLayout(boolean, int, int, int, int)}
     * if the view's bounds has changed or a new Drawable has been set
     * or the {@link DisplayType} has been modified
     */
    protected void onLayoutChanged(int left, int top, int right, int bottom) {
        fireOnLayoutChangeListener(left, top, right, bottom);
    }

    protected float computeMaxZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1F;
        }
        float fw = (float) drawable.getIntrinsicWidth() / (float) mThisWidth;
        float fh = (float) drawable.getIntrinsicHeight() / (float) mThisHeight;
        return Math.max(fw, fh) * 8;
    }

    protected float computeMinZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1F;
        }
        float scale = getScale(mBaseMatrix);
        scale = Math.min(1f, 1f / scale);


        return scale;
    }

    /**
     * Returns the current maximum allowed image scale
     */
    public float getMaxScale() {
        if (mMaxZoom == ZOOM_INVALID) {
            mMaxZoom = computeMaxZoom();
        }
        return mMaxZoom;
    }

    /**
     * Returns the current minimum allowed image scale
     */
    public float getMinScale() {
        if (mMinZoom == ZOOM_INVALID) {
            mMinZoom = computeMinZoom();
        }
        return mMinZoom;
    }

    /**
     * Returns the current view matrix
     */
    public Matrix getImageViewMatrix() {
        return getImageViewMatrix(mSuppMatrix);
    }

    public Matrix getImageViewMatrix(Matrix supportMatrix) {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(supportMatrix);
        return mDisplayMatrix;
    }

    @Override
    public void setImageMatrix(Matrix matrix) {

        Matrix current = getImageMatrix();
        boolean needUpdate = false;

        if (matrix == null && !current.isIdentity() || matrix != null && !current.equals(matrix)) {
            needUpdate = true;
        }

        super.setImageMatrix(matrix);

        if (needUpdate) onImageMatrixChanged();
    }

    /**
     * Called just after a new Matrix has been assigned.
     * {@link #setImageMatrix(Matrix)}
     */
    protected void onImageMatrixChanged() {
    }

    /**
     * Returns the current image display matrix.<br />
     * This matrix can be used in the next call to the {@link #setImageDrawable(Drawable, Matrix, float, float)} to restore the same
     * view state of the previous {@link Bitmap}.<br />
     * Example:
     *
     * <pre>
     * Matrix currentMatrix = mImageView.getDisplayMatrix();
     * mImageView.setImageBitmap( newBitmap, currentMatrix, ZOOM_INVALID, ZOOM_INVALID );
     * </pre>
     *
     * @return the current support matrix
     */
    public Matrix getDisplayMatrix() {
        return new Matrix(mSuppMatrix);
    }

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     */
    protected void getProperBaseMatrix(Drawable drawable, Matrix matrix) {
        float viewWidth = mThisWidth;
        float viewHeight = mThisHeight;
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        float widthScale, heightScale;
        matrix.reset();

        if (w > viewWidth || h > viewHeight) {
            widthScale = viewWidth / w;
            heightScale = viewHeight / h;
            float scale = Math.min(widthScale, heightScale);
            matrix.postScale(scale, scale);

            float tw = (viewWidth - w * scale) / 2.0f;
            float th = (viewHeight - h * scale) / 2.0f;
            matrix.postTranslate(tw, th);

        } else {
            widthScale = viewWidth / w;
            heightScale = viewHeight / h;
            float scale = Math.min(widthScale, heightScale);
            matrix.postScale(scale, scale);

            float tw = (viewWidth - w * scale) / 2.0f;
            float th = (viewHeight - h * scale) / 2.0f;
            matrix.postTranslate(tw, th);
        }

        if (LOG_ENABLED) {
            printMatrix(matrix);
        }
    }

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     */
    protected void getProperBaseMatrix2(Drawable bitmap, Matrix matrix) {

        float viewWidth = mThisWidth;
        float viewHeight = mThisHeight;

        float w = bitmap.getIntrinsicWidth();
        float h = bitmap.getIntrinsicHeight();

        matrix.reset();

        float widthScale = viewWidth / w;
        float heightScale = viewHeight / h;

        float scale = Math.min(widthScale, heightScale);

        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - w * scale) / 2.0f, (viewHeight - h * scale) / 2.0f);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    public void printMatrix(Matrix matrix) {
        float tx = getValue(matrix, Matrix.MTRANS_X);
        float ty = getValue(matrix, Matrix.MTRANS_Y);
    }

    public RectF getBitmapRect() {
        return getBitmapRect(mSuppMatrix);
    }

    protected RectF getBitmapRect(Matrix supportMatrix) {
        final Drawable drawable = getDrawable();

        if (drawable == null) return null;
        Matrix m = getImageViewMatrix(supportMatrix);
        mBitmapRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        m.mapRect(mBitmapRect);
        return mBitmapRect;
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    @SuppressLint("Override")
    public float getRotation() {
        return 0;
    }

    /**
     * Returns the current image scale
     */
    public float getScale() {
        return getScale(mSuppMatrix);
    }

    public float getBaseScale() {
        return getScale(mBaseMatrix);
    }

    @SuppressWarnings("SameParameterValue")
    protected void center(boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();
        if (drawable == null) return;

        RectF rect = getCenter(mSuppMatrix, horizontal, vertical);

        if (rect.left != 0 || rect.top != 0) {
            postTranslate(rect.left, rect.top);
        }
    }

    protected RectF getCenter(Matrix supportMatrix, boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();

        if (drawable == null) return new RectF(0, 0, 0, 0);

        mCenterRect.set(0, 0, 0, 0);
        RectF rect = getBitmapRect(supportMatrix);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            int viewHeight = mThisHeight;
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = mThisHeight - rect.bottom;
            }
        }
        if (horizontal) {
            int viewWidth = mThisWidth;
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        mCenterRect.set(deltaX, deltaY, 0, 0);
        return mCenterRect;
    }

    protected void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            mSuppMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }

    protected void postScale(float scale, float centerX, float centerY) {
        mSuppMatrix.postScale(scale, scale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
    }

    protected PointF getCenter() {
        return mCenter;
    }

    protected void zoomTo(float scale) {
        if (scale > getMaxScale()) scale = getMaxScale();
        if (scale < getMinScale()) scale = getMinScale();
        PointF center = getCenter();
        zoomTo(scale, center.x, center.y);
    }

    /**
     * Scale to the target scale
     *
     * @param scale      the target zoom
     * @param durationMs the animation duration
     */
    public void zoomTo(float scale, float durationMs) {
        PointF center = getCenter();
        zoomTo(scale, center.x, center.y, durationMs);
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > getMaxScale()) scale = getMaxScale();

        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        postScale(deltaScale, centerX, centerY);
        onZoom(getScale());
        center(true, true);
    }

    protected void onZoom(float scale) {
    }

    protected void onZoomAnimationCompleted(float scale) {
    }

    /**
     * Scrolls the view by the x and y amount
     */
    public void scrollBy(float x, float y) {
        panBy(x, y);
    }

    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    protected void updateRect(RectF bitmapRect, RectF scrollRect) {
        if (bitmapRect == null) return;

        if (bitmapRect.top >= 0 && bitmapRect.bottom <= mThisHeight) scrollRect.top = 0;
        if (bitmapRect.left >= 0 && bitmapRect.right <= mThisWidth) scrollRect.left = 0;
        if (bitmapRect.top + scrollRect.top >= 0 && bitmapRect.bottom > mThisHeight)
            scrollRect.top = (int) (0 - bitmapRect.top);
        if (bitmapRect.bottom + scrollRect.top <= (mThisHeight) && bitmapRect.top < 0)
            scrollRect.top = (int) ((mThisHeight) - bitmapRect.bottom);
        if (bitmapRect.left + scrollRect.left >= 0) scrollRect.left = (int) (0 - bitmapRect.left);
        if (bitmapRect.right + scrollRect.left <= mThisWidth)
            scrollRect.left = (int) (mThisWidth - bitmapRect.right);
    }

    protected void scrollBy(float distanceX, float distanceY, final double durationMs) {
        final double dx = distanceX;
        final double dy = distanceY;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {

            double old_x = 0;
            double old_y = 0;

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double currentMs = Math.min(durationMs, now - startTime);
                double x = getEasingEffect().getEasing().easeOut(currentMs, 0, dx, durationMs);
                double y = getEasingEffect().getEasing().easeOut(currentMs, 0, dy, durationMs);
                panBy((x - old_x), (y - old_y));
                old_x = x;
                old_y = y;
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    RectF centerRect = getCenter(mSuppMatrix, true, true);
                    if (centerRect.left != 0 || centerRect.top != 0) scrollBy(centerRect.left, centerRect.top);
                }
            }
        });
    }

    protected void zoomTo(float scale, float centerX, float centerY, final float durationMs) {
        if (scale > getMaxScale()) scale = getMaxScale();

        final long startTime = System.currentTimeMillis();
        final float oldScale = getScale();

        final float deltaScale = scale - oldScale;

        Matrix m = new Matrix(mSuppMatrix);
        m.postScale(scale, scale, centerX, centerY);
        RectF rect = getCenter(m, true, true);

        final float destX = centerX + rect.left * scale;
        final float destY = centerY + rect.top * scale;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float newScale = (float) getEasingEffect().getEasing().easeInOut(currentMs, 0, deltaScale, durationMs);
                zoomTo(oldScale + newScale, destX, destY);
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    onZoomAnimationCompleted(getScale());
                    center(true, true);
                }
            }
        });
    }

    @Override
    public void dispose() {
        clear();
    }
}
