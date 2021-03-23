package com.jjuncoder.sideproject.compass.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import com.jjuncoder.sideproject.R
import kotlin.math.min


class CompassView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context) {

    var bearing: Float = 0f
        set(value) {
            field = value
            invalidate()
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)
        }
    var textHeight: Int = 0

    private lateinit var markerPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var circlePaint: Paint

    private lateinit var northString: String
    private lateinit var southString: String
    private lateinit var eastString: String
    private lateinit var westString: String


    init {
        isFocusable = true
        val a =
            context.obtainStyledAttributes(attributeSet, R.styleable.CompassView, defStyleAttr, 0)
        if (a.hasValue(R.styleable.CompassView_bearing)) {
            bearing = a.getFloat(R.styleable.CompassView_bearing, 0f)
        }
        a.recycle()
        init()
    }

    private fun init() {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.compass_background_color)
            strokeWidth = 1f
            style = Paint.Style.FILL_AND_STROKE
        }

        northString = context.getString(R.string.cardinal_north)
        southString = context.getString(R.string.cardinal_south)
        eastString = context.getString(R.string.cardinal_east)
        westString = context.getString(R.string.cardinal_west)

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.compass_text_color)
            textHeight = measureText("yY").toInt()
        }

        markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.compass_marker_color)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val measureWidth = measure(widthMeasureSpec)
        val measureHeight = measure(heightMeasureSpec)

        val d = Math.min(measureWidth, measureHeight)
        setMeasuredDimension(d, d)
    }

    private fun measure(measureSpec: Int): Int {
        return if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            200
        } else {
            MeasureSpec.getSize(measureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val px = measuredWidth / 2f
        val py = measuredHeight / 2f
        val radius = min(px, py)

        canvas.drawCircle(px, py, radius, circlePaint)
        canvas.save()
        canvas.rotate(-bearing, px, py)

        for (i in 0..24) {
            canvas.drawLine(px, py - radius, px, py - radius + 10, markerPaint)
            canvas.save()
            canvas.translate(0f, textHeight.toFloat())

            if (i % 6 == 0) {
                var dirString = ""
                when (i) {
                    0 -> {
                        dirString = northString
                        val arrowY = (2f * textHeight)
                        canvas.drawLine(px, arrowY, px - 5, 3f * textHeight, markerPaint)
                        canvas.drawLine(px, arrowY, px + 5, 3f * textHeight, markerPaint)
                    }
                    6 -> dirString = eastString
                    12 -> dirString = southString
                    18 -> dirString = westString
                }
                val textWidth = textPaint.measureText("W").toInt()
                val cardinalX = px - textWidth / 2
                val cardinalY = py - radius + textHeight
                canvas.drawText(dirString, cardinalX, cardinalY, textPaint)
            } else if (i % 3 == 0) {
                val angle = (i * 15).toString()
                val angleTextWidth = textPaint.measureText(angle)

                val angleTextX = px - angleTextWidth / 2
                val angleTextY = py - radius + textHeight
                canvas.drawText(angle, angleTextX, angleTextY, textPaint)
            }
            canvas.restore()
            canvas.rotate(15f, px, py)
        }
        canvas.restore()
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        super.dispatchPopulateAccessibilityEvent(event)
        return if (isShown) {
            val bearingStr = bearing.toString()
            event.text.add(bearingStr)
            true
        } else {
            false
        }
    }
}
