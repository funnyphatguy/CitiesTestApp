package com.example.citiestestapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.citiestestapp.R
import com.example.citiestestapp.data.AppDatabase
import com.example.citiestestapp.data.CityListRepository
import com.example.citiestestapp.databinding.ActivityMainBinding
import com.example.citiestestapp.model.CityList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CustomMenuFragment.OnCityListSelectedListener {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, CityListFragment())
                .commit()
        }

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_city_list -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, CityListFragment())
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

        val appContext = applicationContext
        val db = AppDatabase.getInstance(appContext)
        val cityListsViewModel = ViewModelProvider(
            this,
            CityListsViewModel.provideFactory(CityListRepository(db.cityListDao()))
        )[CityListsViewModel::class.java]

        lifecycleScope.launch {
            cityListsViewModel.cityLists.collectLatest { lists ->
                val defaultList = lists.firstOrNull()
                if (defaultList != null) {
                    onCityListSelected(defaultList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCityListSelected(cityList: CityList) {
        android.util.Log.d(
            "MainActivity",
            "onCityListSelected called: ${cityList.shortName}, color: ${cityList.color}"
        )
        binding.bottomNavigation.itemIconTintList = null
        val menu = binding.bottomNavigation.menu
        val item = menu.findItem(R.id.nav_custom_tab)
        item.title = cityList.shortName
        val colorInt = androidx.core.content.ContextCompat.getColor(this, cityList.color)
        android.util.Log.d("MainActivity", "Color resolved to: $colorInt")
        val iconDrawable =
            android.graphics.drawable.ShapeDrawable(android.graphics.drawable.shapes.OvalShape())
        iconDrawable.paint.color = colorInt
        val sizePx = resources.getDimensionPixelSize(R.dimen._40dp)
        iconDrawable.intrinsicWidth = sizePx
        iconDrawable.intrinsicHeight = sizePx
        item.icon = iconDrawable
        binding.bottomNavigation.invalidate()
        android.util.Log.d("MainActivity", "Icon updated")

        val viewModel = ViewModelProvider(this)[CityListViewModel::class.java]
        android.util.Log.d("MainActivity", "Setting city list: ${cityList.cities}")
        viewModel.setCityList(cityList.cities)
    }
} 