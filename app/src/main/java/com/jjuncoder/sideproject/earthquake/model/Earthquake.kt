package com.jjuncoder.sideproject.earthquake.model

import android.location.Location
import java.util.*

data class Earthquake(
    val id: String,
    val date: Date,
    val details: String,
    val location: Location?,
    val magnitude: Double,
    val link: String
)