package com.volvo.drawex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.volvo.drawex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawBtn.setOnClickListener {
            val intent = Intent(this,DrawActivity::class.java)
            startActivity(intent)
        }
    }
}