package com.jjuncoder.sideproject.earthquake.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjuncoder.sideproject.databinding.ActivityEarthQuakeBinding
import com.jjuncoder.sideproject.earthquake.model.Earthquake
import com.jjuncoder.sideproject.earthquake.viewmodel.EarthquakeViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
        binding.captureButton.setOnClickListener {
            captureListViewToImageAndSend()
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

    private fun captureListViewToImageAndSend() {
        val pictureFile = saveBitMap(this, binding.eqRecyclerView)
        val outputUri = Uri.fromFile(pictureFile)
        Log.i("EarthQuakeActivity", "outputUri : $outputUri")
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, outputUri)
        }
        startActivity(Intent.createChooser(intent, "공유 테스트"))
    }

    private fun saveBitMap(context: Context, drawView: View): File? {
        val externalFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.i("EarthQuakeActivity", "externalFileDir : $externalFileDir")

        val pictureFileDir = File(externalFileDir, "sideproject")
        if (!pictureFileDir.exists()) {
            val isDirectoryCreated: Boolean = pictureFileDir.mkdirs()
            if (!isDirectoryCreated) Log.i("EarthQuakeActivity", "Can't create directory to save the image")
            return null
        }

        val filename: String = pictureFileDir.path + File.separator + System.currentTimeMillis().toString() + ".jpg"
        val pictureFile = File(filename)
        val bitmap = getBitmapFromView(drawView)
        try {
            pictureFile.createNewFile()
            FileOutputStream(pictureFile).use {
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("EarthQuakeActivity", "There was an issue saving the image.")
        }

        scanGallery(context, pictureFile.absolutePath)
        return pictureFile
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        Log.i("EarthQuakeActivity", "view width : ${view.width} , height : ${view.height}")
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Draw the view's background
        view.background?.draw(canvas) ?: canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    private fun scanGallery(context: Context, path: String) {
        try {
            MediaScannerConnection.scanFile(context, arrayOf(path), null) { inputPath, outputUri ->
                Log.i("EarthQuakeActivity", "Scan completed. outputPath : $inputPath , outputUri : $outputUri")
                Toast.makeText(context, "Scan completed. outputUri : $outputUri", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("EarthQuakeActivity", "There was an issue scanning gallery.")
        }
    }
}

/**
 * 코로나 API : http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson
 * EndPoint : http://openapi.data.go.kr/openapi/service/rest/Covid19
 * Encoding Key : %2FIGHmr80o%2FEyl9BOwuPpxO00ShtkeL4Gl23PI9X5gC%2BvyF301%2Fvz9U7oCqpYNRnTjFYjrZ%2BTwd89o8Y2sDS8Tg%3D%3D
 * Decoding Key : /IGHmr80o/Eyl9BOwuPpxO00ShtkeL4Gl23PI9X5gC+vyF301/vz9U7oCqpYNRnTjFYjrZ+Twd89o8Y2sDS8Tg==
 * 서비스 인증키 활용 : http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey={EncodingKey}
 */