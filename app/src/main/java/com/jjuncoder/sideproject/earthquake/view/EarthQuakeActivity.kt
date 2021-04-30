package com.jjuncoder.sideproject.earthquake.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjuncoder.sideproject.databinding.ActivityEarthQuakeBinding
import com.jjuncoder.sideproject.earthquake.model.Earthquake
import com.jjuncoder.sideproject.earthquake.viewmodel.EarthquakeViewModel
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
    private val viewModel: EarthquakeViewModel by viewModels()

    private val earthquakes: ArrayList<Earthquake> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOG_TAG, "onCreate")
        binding = ActivityEarthQuakeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()
        initView()
    }

    private fun initView() {
        binding.eqRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EarthQuakeActivity)
            adapter = EarthquakeRecyclerViewAdapter(earthquakes)
        }
        binding.swipeRefreshView.setOnRefreshListener {
            viewModel.updateEarthquakeData()
        }
    }

    private fun initObserver() {
        viewModel.earthquake.observe(this) {
            Log.d(LOG_TAG, "viewModel earthquake list : $it")
            updateEarthquakeList(it)
        }
    }

    private fun updateEarthquakeList(updateList: List<Earthquake>) {
        for (eq in updateList) {
            if (earthquakes.contains(eq) == false) {
                earthquakes.add(eq)
                binding.eqRecyclerView.adapter?.notifyItemInserted(earthquakes.indexOf(eq))
            }
        }
        binding.swipeRefreshView.isRefreshing = false
    }
}

/**
 * 코로나 API : http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson
 * EndPoint : http://openapi.data.go.kr/openapi/service/rest/Covid19
 * Encoding Key : %2FIGHmr80o%2FEyl9BOwuPpxO00ShtkeL4Gl23PI9X5gC%2BvyF301%2Fvz9U7oCqpYNRnTjFYjrZ%2BTwd89o8Y2sDS8Tg%3D%3D
 * Decoding Key : /IGHmr80o/Eyl9BOwuPpxO00ShtkeL4Gl23PI9X5gC+vyF301/vz9U7oCqpYNRnTjFYjrZ+Twd89o8Y2sDS8Tg==
 * 서비스 인증키 활용 : http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey={EncodingKey}
 */