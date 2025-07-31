package com.example.citiestestapp.ui.newList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiestestapp.data.repository.CityListRepository
import com.example.citiestestapp.model.CityListUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListsViewModel @Inject constructor(
    private val repository: CityListRepository
) : ViewModel() {
    val cityLists: Flow<List<CityListUi>> = repository.getAllLists()

    fun addList(list: CityListUi) {
        viewModelScope.launch {
            repository.insertList(list)
        }
    }
}
