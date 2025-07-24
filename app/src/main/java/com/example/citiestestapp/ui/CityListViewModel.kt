package com.example.citiestestapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.citiestestapp.data.City

class CityListViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    init {
        println("ViewModel created! Saved state: ${savedStateHandle.get<List<City>>(CITIES_KEY)}")
    }

    companion object {
        private const val CITIES_KEY = "cities"
    }

    private val _cityList = savedStateHandle.getLiveData<List<City>>(CITIES_KEY, getDefaultCities())
    val cityList = _cityList

    private fun getDefaultCities() = mutableListOf<City>(
        City("Париж", "III век до н.э."),
        City("Вена", "1147 год"),
        City("Берлин", "1237 год"),
        City("Варшава", "1321 год"),
        City("Милан", "1899 год")
    )

    fun swapItems(from: Int, to: Int) {
        val currentList = _cityList.value?.toMutableList() ?: return

        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)

            println("Swapped items: $from -> $to. New list: $currentList")
            savedStateHandle[CITIES_KEY] = currentList
        }
    }

    fun setCityList(newList: List<City>) {
        savedStateHandle[CITIES_KEY] = newList
    }
}