package com.celestial.progress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.celestial.progress.ui.component.BigProgressBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val progressBar = findViewById<BigProgressBar>(R.id.customProgress)
    }
}