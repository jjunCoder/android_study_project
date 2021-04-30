package com.jjuncoder.sideproject.gesture.widget.zoom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

/**
 * 사용법
 * LinearLayout layout = new LinearLayout(this);
 * ImageViewTouch touch = new ImageViewTouch(this);
 * Matrix matrix = new Matrix();
 * touch.setImageDrawable(getResources().getDrawable(R.drawable.f_1920_1200), matrix, 0.2f, 5f);
 * layout.addView(touch, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
 *
 * @author driverjongho
 */
public class ImageViewTouch extends ImageViewTouchBase {
    private static String TAG = "ImageViewTouch";

    static final float SCROLL_DELTA_THRESHOLD = 1.0f;
    static final int DOUBLE_TAP_ZOOM_IN = 1;
    static final int DOUBLE_TAP_ZOOM_OUT = -1;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected int mDoubleTapDirection;
    protected OnGestureListener mGestureListener;
    protected OnScaleGestureListener mScaleGestureListener;
    protected boolean mDoubleTapEnabled = true;
    protected boolean mScaleEnabled = true;
    protected boolean mScrollEnabled = true;
    private OnImageViewTouchDoubleTapListener mDoubleTapListener;
    private OnImageViewTouchSingleTapListener mSingleTapListener;
    private OnImageViewTouchFlingCloseListener mFlingCloseListener;
    private OnImageViewTouchScaleListener mScaleListener;
    private boolean mIsDoubleTapping = false;

    private OnImageViewTouchScaleListener.ScaleState mOldScaledState = OnImageViewTouchScaleListener.ScaleState.NO_SCALE;

    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTouch(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    protected void init() {
        super.init();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mGestureListener = getGestureListener();
        mScaleGestureListener = getScaleListener();

        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleGestureListener);
        if (!this.isInEditMode()) {
            mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);
        }
        mDoubleTapDirection = 1;
    }

    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    public void setSingleTapListener(OnImageViewTouchSingleTapListener listener) {
        mSingleTapListener = listener;
    }

    public void setFlingCloseListener(OnImageViewTouchFlingCloseListener listener) {
        mFlingCloseListener = listener;
    }

    public void setScaleUpListener(OnImageViewTouchScaleListener listener) {
        mScaleListener = listener;
    }

    public void setDoubleTapEnabled(boolean value) {
        mDoubleTapEnabled = value;
    }

    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    public boolean getDoubleTapEnabled() {
        return mDoubleTapEnabled;
    }

    protected OnGestureListener getGestureListener() {
        return new GestureListener();
    }

    protected OnScaleGestureListener getScaleListener() {
        return new ScaleListener();
    }

    @Override
    protected void _setImageDrawable(final Drawable drawable, final Matrix initial_matrix, float min_zoom, float max_zoom) {
        super._setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }

        int action = event.getAction();
        if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (getScale() < getMinScale()) {
                zoomTo(getMinScale(), 50);
            }
            if (mIsDoubleTapping) {
                mIsDoubleTapping = false;
            }
        } else if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            if (mIsDoubleTapping == false && mZoomToAnimation != null) {
                mZoomToAnimation.cancel();
            }
        }
        return true;
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {
        if (scale < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }

        onScaleStateChanged();
    }

    protected float onDoubleTapPost(float scale) {
        if (mDoubleTapDirection == DOUBLE_TAP_ZOOM_IN) {
            if (scale > 1f) {
                return 1f;
            } else {
                mDoubleTapDirection = DOUBLE_TAP_ZOOM_OUT;
                return 2f;
            }
        } else {
            mDoubleTapDirection = DOUBLE_TAP_ZOOM_IN;
            return 1f;
        }
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mScrollEnabled) {
            return false;
        }

        if (e1 == null || e2 == null) {
            return false;
        }
        if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
            return false;
        }
        if (mScaleDetector.isInProgress()) {
            return false;
        }
//        if (getScale() == 1f) {
//            return false;
//        }

        mUserScaled = true;
        scrollBy(-distanceX, -distanceY);
        invalidate();
        return true;
    }

    private void onFlingClose() {
        if (mFlingCloseListener != null) {
            mFlingCloseListener.onFlingClose();
        }

    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        //최초사이즈에서 플링이 일어나면 스크롤이 어차피 안되니 닫기위해 던지는 액션으로 간주. - harris.kim@sk.com
        if (Float.compare(getScale(), getMinScale()) == 0 && (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800)) {
            onFlingClose();
            return true;
        }

        if (!mScrollEnabled) {
            return false;
        }

        if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
            return false;
        }
        if (mScaleDetector.isInProgress()) {
            return false;
        }
        if (getScale() == 1f) {
            return false;
        }

        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            mUserScaled = true;
            scrollBy(diffX / 2, diffY / 2, 300);
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction - positive direction value means scroll from right to left,
     *                  negative value means scroll from left to right
     * @return true if there is some more place to scroll, false - otherwise.
     */
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (null == bitmapRect) {
            return false;
        }

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollRect.left);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (mSingleTapListener != null) {
                mSingleTapListener.onSingleTapConfirmed();
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mIsDoubleTapping = true;
//            if (LogExUtils.isLogExInfo()) {
//                LogEx.i(TAG, "onDoubleTap. double tap enabled? " + mDoubleTapEnabled);
//            }
            if (mDoubleTapEnabled) {
                mUserScaled = true;

                float scale = getScale();
                float targetScale = onDoubleTapPost(scale);
                targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale()));
                zoomToAnimation(targetScale, e.getX(), e.getY(), mDoubleTapZoomInOutAniDuration);
                invalidate();

                if (mScaleListener != null) {
                    if (targetScale == getInitScale()) {
                        mScaleListener.onPreScaleStateChange(OnImageViewTouchScaleListener.ScaleState.NO_SCALE);
                    } else if (targetScale < getInitScale()) {
                        mScaleListener.onPreScaleStateChange(OnImageViewTouchScaleListener.ScaleState.SCALE_DOWN);
                    } else {
                        mScaleListener.onPreScaleStateChange(OnImageViewTouchScaleListener.ScaleState.SCALE_UP);
                    }
                }
            }

            if (null != mDoubleTapListener) {
                mDoubleTapListener.onDoubleTap();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress()) {
                    setPressed(true);
                    performLongClick();
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return ImageViewTouch.this.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return ImageViewTouch.this.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void onScaleStateChanged() {
        int scaleCompareResult = Float.compare(getScale(), getInitScale());
        if (scaleCompareResult == 0 && mOldScaledState != OnImageViewTouchScaleListener.ScaleState.NO_SCALE) {
            mOldScaledState = OnImageViewTouchScaleListener.ScaleState.NO_SCALE;
        } else if (scaleCompareResult > 0 && mOldScaledState != OnImageViewTouchScaleListener.ScaleState.SCALE_UP) {
            mOldScaledState = OnImageViewTouchScaleListener.ScaleState.SCALE_UP;
        } else if (scaleCompareResult < 0 && mOldScaledState != OnImageViewTouchScaleListener.ScaleState.SCALE_DOWN) {
            mOldScaledState = OnImageViewTouchScaleListener.ScaleState.SCALE_DOWN;
        }

        if (mScaleListener != null) {
            mScaleListener.onScaleStateChanged(mOldScaledState);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        protected boolean mScaled = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = getScale() * detector.getScaleFactor();

            if (mScaleEnabled) {
                if (mScaled && span != 0) {
                    mUserScaled = true;
                    targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale() - 0.1f));
                    zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                    mDoubleTapDirection = 1;
                    invalidate();

                    onScaleStateChanged();
                    return true;
                }

                // This is to prevent a glitch the first time
                // image is scaled.
                if (!mScaled) {
                    mScaled = true;
                }
            }
            return true;
        }
    }

    public interface OnImageViewTouchDoubleTapListener {

        void onDoubleTap();
    }

    public interface OnImageViewTouchSingleTapListener {

        void onSingleTapConfirmed();
    }

    public interface OnImageViewTouchFlingCloseListener {

        void onFlingClose();
    }

    public interface OnImageViewTouchScaleListener {
        enum ScaleState {
            SCALE_UP,
            SCALE_DOWN,
            NO_SCALE,
        }

        void onPreScaleStateChange(ScaleState scaleState);

        void onScaleStateChanged(ScaleState scaleState);
    }
}
