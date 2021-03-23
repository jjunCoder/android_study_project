package com.jjuncoder.sideproject.compass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jjuncoder.sideproject.R

class CompassActivity : AppCompatActivity() {
    companion object {
        const val LOG_TAG = "CompassActivity"

        fun startCompassActivity(context: Context) {
            val intent = Intent(context, CompassActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

    }
}