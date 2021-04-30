package com.jjuncoder.sideproject.gesture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.jjuncoder.sideproject.gesture.widget.zoom.FastBitmapDrawable
import com.jjuncoder.sideproject.gesture.widget.zoom.ImageViewTouch
import kotlin.math.floor
import kotlin.math.max

class GestureImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageViewTouch(context, attrs, defStyleAttr) {

    companion object {
        private const val LOG_TAG = "GestureImageView"
    }

    interface OnMatrixChangeListener {
        fun onImageMatrixChanged(bitmap: Bitmap, rectInBitmap: RectF)
    }

    var listener: OnMatrixChangeListener? = null

    init {
        displayType = DisplayType.FIT_TO_SCREEN
    }

    private val tempInverse = Matrix()


    override fun onImageMatrixChanged(imageMatrix: Matrix?) {
        val drawable = drawable
        if (drawable is FastBitmapDrawable) {
            imageViewMatrix.invert(tempInverse)
            val rectInBitmap = RectF(0f, 0f, width.toFloat(), height.toFloat())
            tempInverse.mapRect(rectInBitmap)
            rectInBitmap.apply {
                right = floor(right)
                bottom = floor(bottom)
            }
//            LogExDebug(LOG_TAG) { "[onImageMatrixChanged] rectInBitmap: $rectInBitmap" }
            listener?.onImageMatrixChanged(drawable.bitmap, rectInBitmap)
        }
    }

    override fun getProperBaseMatrix(
        viewWidth: Int,
        viewHeight: Int,
        drawable: Drawable,
        matrix: Matrix
    ) {
        //super.getProperBaseMatrix(viewWidth, viewHeight, drawable, matrix)

//        LogExDebug(LOG_TAG) { "[getProperBaseMatrix] view: ${viewWidth}x${viewHeight}" }

        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        val widthScale: Float
        val heightScale: Float
        matrix.reset()

        widthScale = viewWidth / w
        heightScale = viewHeight / h
        val scale = max(widthScale, heightScale) //  IMPL_NOTE 부모와 다르게 min 아닌 max를 취합니다
        matrix.postScale(scale, scale)
        val tw = (viewWidth - w * scale) / 2.0f
        val th = (viewHeight - h * scale) / 2.0f
        matrix.postTranslate(tw, th)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        LogExDebug(LOG_TAG) { "[onSizeChanged] ${w}x${h}" }
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        super.setImageBitmap(bitmap)

        if (bitmap == null) {
            // 이미지 삭제 시 호출, ImageView 의 matrix 를 초기화합니다
            imageMatrix = null
        }
    }
}