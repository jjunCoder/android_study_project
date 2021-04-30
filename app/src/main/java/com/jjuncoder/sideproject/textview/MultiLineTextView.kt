package com.jjuncoder.sideproject.textview

import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.StaticLayout
import android.text.style.BackgroundColorSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import com.jjuncoder.sideproject.R

class MultiLineTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "MultiLineTextView"
    }

    private val imageSpan = ImageSpan(context, R.drawable.ic_launcher_background)
    private var mFullText: CharSequence? = null
    private var mResourceId: Int = R.drawable.ic_launcher_background

    fun setTextWithImage() {
        if (text.length > 1) {
            val spannable = SpannableString(text)
            spannable.setSpan(
                imageSpan,
                text.length - 1,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setText(spannable, BufferType.SPANNABLE)
        }
    }

    fun setTextWithBackground() {
        if (text.length > 2) {
            val spannable = SpannableString(text)
            spannable.setSpan(
                BackgroundColorSpan(Color.RED),
                text.length - 2,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setText(spannable, BufferType.SPANNABLE)
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        Log.d(TAG, "setText : $text")
        mFullText = text
        resetText()
    }

    private fun resetText() {
        mFullText?.let {
            var workingText = it as CharSequence
            val layout = createWorkingLayout(workingText)
        }
    }

    private fun createWorkingLayout(workingText: CharSequence): Layout {
        val builder = StaticLayout.Builder.obtain(workingText, 0, text.length, paint, width)
        return builder.build()
    }
}