package com.example.citiestestapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.citiestestapp.model.City
import com.example.citiestestapp.model.CityListEntity
import com.example.citiestestapp.data.CityListRepository
import com.example.citiestestapp.model.toDomain
import com.example.citiestestapp.model.toEntity
import com.example.citiestestapp.model.CityList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class CityListsViewModel(private val repository: CityListRepository) : ViewModel() {
    val cityLists: StateFlow<List<CityList>> =
        repository.getAllLists()
            .map { list -> list.map { it.toDomain() } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            repository.getAllLists().collect { lists ->
                if (lists.isEmpty()) {
                    repository.insertList(getDefaultEuropeList().toEntity())
                }
                cancel()
            }
        }
    }

    fun addList(list: CityList) {
        viewModelScope.launch {
            repository.insertList(list.toEntity())
        }
    }


    private fun getDefaultEuropeList(): CityList {
        return CityList(
            shortName = "Европа",
            fullName = "Города Европы",
            color = com.example.citiestestapp.R.color.color_blue,
            cities = listOf(
                City("Париж", "III век до н.э."),
                City("Вена", "1147 год"),
                City("Берлин", "1237 год"),
                City("Варшава", "1321 год"),
                City("Милан", "1899 год")
            )
        )
    }

    companion object {
        fun provideFactory(repository: CityListRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CityListsViewModel(repository) as T
                }
            }
    }
}