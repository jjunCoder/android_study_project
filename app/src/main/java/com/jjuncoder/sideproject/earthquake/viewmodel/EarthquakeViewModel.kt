package com.jjuncoder.sideproject.earthquake.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjuncoder.sideproject.earthquake.EarthquakeDataLoader
import com.jjuncoder.sideproject.earthquake.model.Earthquake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EarthquakeViewModel : ViewModel() {
    companion object {
        const val TAG = "EarthquakeViewModel"
    }

    private val _earthquakes = MutableLiveData(ArrayList<Earthquake>())
    val earthquake: LiveData<ArrayList<Earthquake>>
        get() = _earthquakes

    init {
        updateEarthquakeData()
    }

    fun updateEarthquakeData() {
        viewModelScope.launch {
            _earthquakes.value = withContext(Dispatchers.IO) {
                EarthquakeDataLoader.loadEarthquakes()
            }
        }
    }
}