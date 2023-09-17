package com.example.nikestore.feature.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.nikestore.databinding.ActivityStartBinding

class StartActivity: AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private var startActivityIsStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStartBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!startActivityIsStarted) {
                this.startActivity(Intent(this, MainActivity::class.java))
                this.finish()
                startActivityIsStarted = true
            }
        }, 2500)
    }
}