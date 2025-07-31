package com.example.citiestestapp.ui

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.ActivityMainBinding
import com.example.citiestestapp.model.CityListUi
import com.example.citiestestapp.ui.cities.CitiesListFragment
import com.example.citiestestapp.ui.cities.CitiesViewModel
import com.example.citiestestapp.ui.newList.CityListsViewModel
import com.example.citiestestapp.ui.selector.ListSelectorFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnCityListSelectedListener {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: CityListsViewModel by viewModels()

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
            viewModel.cityLists.collectLatest { lists ->
                lists.firstOrNull()?.let { onCityListSelected(it) }
            }
        }
    }

    override fun onCityListSelected(cityList: CityListUi) {
        Log.d("MainActivity", "onCityListSelected: ${cityList.shortName}, color=${cityList.color}")

        binding.bottomNavigation.apply {
            itemIconTintList = null
            menu.findItem(R.id.nav_custom_tab).apply {
                title = cityList.shortName
                val colorInt = ContextCompat.getColor(this@MainActivity, cityList.color)
                Log.d("MainActivity", "Color resolved to: $colorInt")
                icon = ShapeDrawable(OvalShape()).apply {
                    paint.color = colorInt
                    val size = resources.getDimensionPixelSize(R.dimen._40dp)
                    intrinsicWidth = size
                    intrinsicHeight = size
                }
            }
            invalidate()
        }

        ViewModelProvider(this)[CitiesViewModel::class.java].also { vm ->
            Log.d("MainActivity", "Setting city list: ${cityList.cities}")
            vm.setCityList(cityList.cities)
        }
    }
}
