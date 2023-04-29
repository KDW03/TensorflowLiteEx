package com.volvo.drawex

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.volvo.drawex.databinding.ActivityDrawBinding
import java.util.*

class DrawActivity : AppCompatActivity() {

    lateinit var cls: Classifier
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityDrawBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        cls = Classifier(this)

        with(binding.drawView) {
            setStrokeWidth(100.0f) // 글씨 두께 100
            setBackgroundColor(Color.BLACK) // 검은색 배경
            setColor(Color.WHITE) // 흰 글씨
        }

        binding.classifyBtn.setOnClickListener {
            val image = binding.drawView.getBitmap() // 현재 drawView를 Bitmap 객체로 가져옴
            val res = cls.classify(image)
            val outStr = String.format(Locale.ENGLISH, "%d, %.0f%%", res.first, res.second * 100.0f)
            Log.d("classTest",outStr)
            binding.resultView.text = outStr
        }

        binding.clearBtn.setOnClickListener {
            binding.drawView.clearCanvas() // clear
        }


    }

    override fun onDestroy() {
        cls.finish()
        super.onDestroy()
    }
}