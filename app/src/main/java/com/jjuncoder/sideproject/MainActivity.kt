package com.jjuncoder.sideproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jjuncoder.sideproject.compass.CompassActivity
import com.jjuncoder.sideproject.databinding.ActivityMainBinding
import com.jjuncoder.sideproject.earthquake.view.EarthQuakeActivity
import com.jjuncoder.sideproject.gesture.ImageGestureTestActivity
import com.jjuncoder.sideproject.textview.TextViewTestActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.earthquakeActivityButton.setOnClickListener {
            EarthQuakeActivity.startEarthQuakeActivity(this)
        }

        binding.compassActivityButton.setOnClickListener {
            CompassActivity.startCompassActivity(this)
        }

        binding.textViewActivityButton.setOnClickListener {
            TextViewTestActivity.startActivity(this)
        }

        binding.imageGestureViewActivityButton.setOnClickListener {
            ImageGestureTestActivity.startActivity(this)
        }
    }
}