package com.celestial.progress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.celestial.progress.data.adapter.ItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    
    }
}