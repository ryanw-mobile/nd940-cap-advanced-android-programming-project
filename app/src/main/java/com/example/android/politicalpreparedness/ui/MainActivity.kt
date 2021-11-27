package com.example.android.politicalpreparedness.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppbar)

        // We are using a fixed App name heading, but wish to have the back button
        val navController = findNavController(R.id.nav_host)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.topAppbar.setupWithNavController(navController, appBarConfiguration)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}
