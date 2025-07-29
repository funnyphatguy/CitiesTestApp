package com.example.citiestestapp.ui.newList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.citiestestapp.R
import com.example.citiestestapp.data.CityListRepository
import com.example.citiestestapp.model.City
import com.example.citiestestapp.model.CityList
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CityListsViewModel(private val repository: CityListRepository) : ViewModel() {
    val cityLists: StateFlow<List<CityList>> =
        repository.getAllLists()
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    init {
        viewModelScope.launch {
            repository.getAllLists().collect { lists ->
                if (lists.isEmpty()) {
                    repository.insertList(getDefaultEuropeList())
                }
                cancel()
            }
        }
    }

    fun addList(list: CityList) {
        viewModelScope.launch {
            repository.insertList(list)
        }
    }

    private fun getDefaultEuropeList(): CityList {
        return CityList(
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