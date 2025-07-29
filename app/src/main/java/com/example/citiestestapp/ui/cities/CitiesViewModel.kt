package com.example.citiestestapp.ui.cities

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.citiestestapp.model.City

class CitiesViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    init {
        println("ViewModel created! Saved state: ${savedStateHandle.get<List<City>>(CITIES_KEY)}")
    }

    private val _cityList = savedStateHandle.getLiveData<List<City>>(
        CITIES_KEY, getDefaultCities()
    )
    val cityList = _cityList

    private fun getDefaultCities() = mutableListOf(
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
        Log.d(
            "CityListViewModel", "setCityList called with: $newList"
        )
        savedStateHandle[CITIES_KEY] = newList
        Log.d(
            "CityListViewModel", "Updated cityList value: ${_cityList.value}"
        )
    }

    companion object {
        private const val CITIES_KEY = "cities"
    }
}