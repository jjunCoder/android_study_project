package com.jjuncoder.sideproject.earthquake

import android.location.Location
import android.util.Log
import androidx.annotation.WorkerThread
import com.jjuncoder.sideproject.earthquake.model.Earthquake
import com.jjuncoder.sideproject.earthquake.viewmodel.EarthquakeViewModel
import org.w3c.dom.Element
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

object EarthquakeDataLoader {
    const val FEED_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.atom"

    @WorkerThread
    fun loadEarthquakes(): ArrayList<Earthquake> {
        val result = ArrayList<Earthquake>()
        try {
            val url = URL(FEED_URL)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val dom = documentBuilder.parse(urlConnection.inputStream)
                val element = dom.documentElement

                val nodeList = element.getElementsByTagName("entry")
                if (nodeList != null && nodeList.length > 0) {
                    for (i in 0..nodeList.length) {
                        Log.d(EarthquakeViewModel.TAG, nodeList.toString())
                        nodeList.item(i).let {
                            if (it != null) {
                                Log.d(EarthquakeViewModel.TAG, it.toString())
                                result.add(parseNode(it as Element))
                            }
                        }
                    }
                }
            }
            urlConnection.disconnect()

        } catch (e: MalformedURLException) {
            Log.e(EarthquakeViewModel.TAG, "MalformedURLException", e)
        } catch (e: Exception) {
            Log.e(EarthquakeViewModel.TAG, "other exception", e)
        }
        return result
    }

    private fun parseNode(entry: Element): Earthquake {
        entry.apply {
            val id = getElementsByTagName("id").item(0).firstChild.nodeValue
            val geo = getElementsByTagName("georss:point").item(0)
            val updatedWhen = getElementsByTagName("updated").item(0)
            val link = getElementsByTagName("link").item(0).attributes
            val hostname = "http://earthquake.usgs.gov"
            val linkString = hostname + link.getNamedItem("href")
            val point = geo.firstChild.nodeValue
            val date = updatedWhen.firstChild.nodeValue
            val dateFormatted: Date =
                try {
                    SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.KOREA).parse(date)
                        ?: GregorianCalendar(0, 0, 0).time
                } catch (e: Exception) {
                    Log.e(EarthquakeViewModel.TAG, "Date parsing exception", e)
                    GregorianCalendar(0, 0, 0).time
                }
            val location = point.split(" ")
            val dummyLocation = Location("dummyGPS").apply {
                latitude = location[0].toDouble()
                longitude = location[1].toDouble()
            }
            val details = getElementsByTagName("title").item(0).firstChild.nodeValue.apply {
                if (contains("-")) {
                    split("-")[1].trim()
                }
            }
            val magnitudeString = details.split(" ")[1]
            val magnitude =
                magnitudeString.substring(0, magnitudeString.length - 1)
                    .toDouble()


            val earthquake =
                Earthquake(
                    id,
                    dateFormatted,
                    details,
                    dummyLocation,
                    magnitude,
                    linkString
                )
            Log.d(EarthquakeViewModel.TAG, "earthquake : $earthquake")
            return earthquake
        }
    }
}