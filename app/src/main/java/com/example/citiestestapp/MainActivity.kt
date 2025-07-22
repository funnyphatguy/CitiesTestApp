package com.example.citiestestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.citiestestapp.databinding.ActivityMainBinding
import com.example.citiestestapp.ui.CityListFragment
import com.example.citiestestapp.ui.CustomMenuFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CityListFragment())
                .commit()
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_city_list -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CityListFragment())
                        .commit()
                    true
                }

                R.id.nav_custom_tab -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CustomMenuFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}

