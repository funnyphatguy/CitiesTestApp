package com.example.citiestestapp.ui.cities

import androidx.lifecycle.ViewModel
import com.example.citiestestapp.data.repository.CityListRepository
import com.example.citiestestapp.model.CityListUi
import com.example.citiestestapp.model.CityUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val repository: CityListRepository
) : ViewModel() {

    private val _cityLists = MutableStateFlow<List<CityUi>>(emptyList())
    val cityList: StateFlow<List<CityUi>> = _cityLists

    val citiesForActivity: Flow<List<CityListUi>> = repository.getAllLists()

    fun swapItems(from: Int, to: Int) {
        val currentList = cityList.value.toMutableList()

        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)
            _cityLists.value = currentList
        }
    }

    fun setCityList(newList: List<CityUi>) {
        _cityLists.value = newList
    }
}
