package com.jjuncoder.sideproject.textview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.jjuncoder.sideproject.databinding.ActivityTextViewTestBinding

class TextViewTestActivity : AppCompatActivity() {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, TextViewTestActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private lateinit var binding: ActivityTextViewTestBinding

    var clickBaseText: String = ""
    var testText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextViewTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.clickBaseButton.setOnClickListener {
            binding.clickBaseTextView.setText(binding.clickBaseEditText.text.toString(), TextView.BufferType.SPANNABLE)
            binding.clickBaseTextView.setTextWithImage()

            binding.testTextView.setTextWithBackground()
        }

        binding.testEditText.doAfterTextChanged {
            binding.testTextView.text = it
        }
    }
}