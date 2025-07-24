package com.example.citiestestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.citiestestapp.databinding.ActivityMainBinding
import com.example.citiestestapp.ui.CityListFragment
import com.example.citiestestapp.ui.CustomMenuFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.ViewModelProvider
import com.example.citiestestapp.data.CityList
import com.example.citiestestapp.ui.CityListViewModel

class MainActivity : AppCompatActivity(), CustomMenuFragment.OnCityListSelectedListener {

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
                    val fragment = CustomMenuFragment()
                    fragment.show(supportFragmentManager, "CustomMenuFragment")
                    false
                }

                else -> false
            }
        }
    }

    override fun onCityListSelected(cityList: CityList) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val menu = bottomNavigation.menu
        val item = menu.findItem(R.id.nav_custom_tab)
        item.title = cityList.shortName
        val iconDrawable = resources.getDrawable(R.drawable.ic_custom_circle, null).mutate()
        if (iconDrawable is android.graphics.drawable.ShapeDrawable) {
            iconDrawable.paint.color = cityList.color
        } else if (iconDrawable is android.graphics.drawable.GradientDrawable) {
            iconDrawable.setColor(cityList.color)
        }
        item.icon = iconDrawable
        val viewModel = ViewModelProvider(this).get(CityListViewModel::class.java)
        viewModel.setCityList(cityList.cities)
    }
}

