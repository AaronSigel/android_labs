package com.example.labs_app.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.labs_app.databinding.ActivityMainBinding

/**
 * Единственная Activity приложения. Хостит NavHostFragment с графом навигации.
 * Навигация выполняется через Navigation Component во фрагментах.
 */
class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
