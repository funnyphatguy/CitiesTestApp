package com.example.citiestestapp.ui

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.ActivityMainBinding
import com.example.citiestestapp.model.CityListUi
import com.example.citiestestapp.ui.cities.CitiesListFragment
import com.example.citiestestapp.ui.cities.CitiesViewModel
import com.example.citiestestapp.ui.selector.ListSelectorFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnCityListSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CitiesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_city_list -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, CitiesListFragment())
                        .commit()
                    true
                }

                R.id.nav_custom_tab -> {
                    ListSelectorFragment().show(supportFragmentManager, "CustomMenuFragment")
                    false
                }

                else -> false
            }
        }

        lifecycleScope.launch {
            viewModel.citiesForActivity.collect { lists ->
                lists.firstOrNull()?.let { onCityListSelected(it) }
            }
        }
    }

    override fun onCityListSelected(cityList: CityListUi) {
        viewModel.setCityList(cityList.cities)
        binding.bottomNavigation.apply {
            itemIconTintList = null
            menu.findItem(R.id.nav_custom_tab).apply {
                title = cityList.name
                val colorInt = ContextCompat.getColor(this@MainActivity, cityList.color)
                Log.d("MainActivity", "Color resolved to: $colorInt")
                icon = ShapeDrawable(OvalShape()).apply {
                    paint.color = colorInt
                    val size = resources.getDimensionPixelSize(R.dimen.topBar_right_size)
                    intrinsicWidth = size
                    intrinsicHeight = size
                }
            }
        }
    }
}
