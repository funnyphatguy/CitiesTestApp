package com.example.citiestestapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.citiestestapp.R
import com.example.citiestestapp.data.City
import com.example.citiestestapp.data.CityList

class CityListsViewModel : ViewModel() {
    private val _cityLists = MutableLiveData(mutableListOf(getDefaultEuropeList()))
    val cityLists: LiveData<MutableList<CityList>> = _cityLists

    fun addList(list: CityList) {
        val current = _cityLists.value ?: mutableListOf()
        current.add(list)
        _cityLists.value = current
    }

    fun getList(index: Int): CityList? = _cityLists.value?.getOrNull(index)

    fun getAllLists(): List<CityList> = _cityLists.value ?: emptyList()

    companion object {
        fun getDefaultEuropeList() = CityList(
            shortName = "Европа",
            fullName = "Города Европы",
            color = R.color.color_blue,
            cities = listOf(
                City("Париж", "III век до н.э."),
                City("Вена", "1147 год"),
                City("Берлин", "1237 год"),
                City("Варшава", "1321 год"),
                City("Милан", "1899 год")
            )
        )
    }
}