package com.jjuncoder.sideproject.gesture

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jjuncoder.sideproject.R
import com.jjuncoder.sideproject.databinding.ActivityImageGestureTestBinding

class ImageGestureTestActivity : AppCompatActivity() {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ImageGestureTestActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private lateinit var binding: ActivityImageGestureTestBinding
    private val tempRect: Rect by lazy { Rect() }
    private var touchTargetView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageGestureTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.iu)
        val imageTouchView = binding.imageTouchView
        imageTouchView.setImageBitmap(bitmap)
    }

}