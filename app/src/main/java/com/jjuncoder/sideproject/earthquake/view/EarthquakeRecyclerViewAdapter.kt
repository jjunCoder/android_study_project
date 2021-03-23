package com.jjuncoder.sideproject.earthquake.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jjuncoder.sideproject.databinding.ListItemEarthquakeBinding
import com.jjuncoder.sideproject.earthquake.model.Earthquake
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeRecyclerViewAdapter(private val list: List<Earthquake>) :
    RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemEarthquakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.earthquake = list[position]
        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int = list.size
}

class ViewHolder(val binding: ListItemEarthquakeBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.timeFormat = TIME_FORMAT
        binding.magnitudeFormat = MAGNITUDE_FORMAT
    }

    companion object {
        val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.KOREA)
        val MAGNITUDE_FORMAT = DecimalFormat("0.0")
    }
}
