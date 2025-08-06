package com.example.citiestestapp.ui.newList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiestestapp.data.database.CityPreset
import com.example.citiestestapp.data.repository.CityListRepository
import com.example.citiestestapp.model.CityListUi
import com.example.citiestestapp.model.CityUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListsViewModel @Inject constructor(
    private val repository: CityListRepository
) : ViewModel() {

    val cities: List<CityUi>
        get() = CityPreset.entries.map { it.toUi() }

    fun addList(list: CityListUi) {
        viewModelScope.launch {
            repository.insertList(list)
        }
    }
}
