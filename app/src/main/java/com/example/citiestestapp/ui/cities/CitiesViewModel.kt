package com.example.citiestestapp.ui.cities

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.citiestestapp.model.CityUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val cityList: StateFlow<List<CityUi>> = savedStateHandle.getStateFlow(
        key = CITIES_KEY,
        initialValue = emptyList()
    )

    fun swapItems(from: Int, to: Int) {
        val currentList = cityList.value.toMutableList()

        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)

            savedStateHandle[CITIES_KEY] = currentList
        }
    }

    fun setCityList(newList: List<CityUi>) {
        savedStateHandle[CITIES_KEY] = newList
    }

    companion object {
        private const val CITIES_KEY = "cities"
    }
}