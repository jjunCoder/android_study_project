package com.jjuncoder.sideproject.earthquake.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjuncoder.sideproject.databinding.ActivityEarthQuakeBinding
import com.jjuncoder.sideproject.earthquake.model.Earthquake
import java.util.*

class EarthQuakeActivity : AppCompatActivity() {
    companion object {
        const val LOG_TAG = "EarthQuakeActivity"

        fun startEarthQuakeActivity(context: Context) {
            val intent = Intent(context, EarthQuakeActivity::class.java)
            startActivity(context, intent, null)
        }
    }

    lateinit var binding: ActivityEarthQuakeBinding

    private val earthquakes: ArrayList<Earthquake> = arrayListOf(
        Earthquake(
            "0",
            Calendar.getInstance().time,
            "earthquake id 0 : seoul",
            null,
            0.0,
            "https://example.com"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOG_TAG, "onCreate")
        binding = ActivityEarthQuakeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        updateEarthquakeList(
            arrayListOf(
                Earthquake(
                    "1",
                    Calendar.getInstance().time,
                    "earthquake id 1 : LA",
                    null,
                    5.0,
                    "https://example.com"
                )
            )
        )
    }

    private fun initView() {
        binding.eqRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EarthQuakeActivity)
            adapter = EarthquakeRecyclerViewAdapter(earthquakes)
        }
    }

    fun updateEarthquakeList(updateList: List<Earthquake>) {
        for (eq in updateList) {
            if (earthquakes.contains(eq) == false) {
                earthquakes.add(eq)
                binding.eqRecyclerView.adapter?.notifyItemInserted(earthquakes.indexOf(eq))
            }
        }
    }
}