package com.example.android.politicalpreparedness.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.politicalpreparedness.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}
