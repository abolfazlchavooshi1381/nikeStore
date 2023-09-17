package com.example.nikestore.feature.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nikestore.R
import com.example.nikestore.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityAuthBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, LoginFragment())
        }.commit()
    }
}