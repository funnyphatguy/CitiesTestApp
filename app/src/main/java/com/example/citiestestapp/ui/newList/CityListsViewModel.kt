package com.example.citiestestapp.ui.newList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.citiestestapp.data.repository.CityListRepository
import com.example.citiestestapp.model.CityListUi
import com.example.citiestestapp.ui.selector.SelectorScreenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CityListsViewModel(
    private val repository: CityListRepository
) : ViewModel() {

    val cityLists: Flow<List<CityListUi>> = repository.getAllLists()

    private val selected: MutableStateFlow<CityListUi?> = MutableStateFlow(null)

    val selectorScreenState: StateFlow<SelectorScreenModel> = selected
        .combine(cityLists) { selected, cityLists ->
            // todo map to newModel for adapter - add selected Flag for it
            SelectorScreenModel(
                cities = cityLists,
                selectedItem = cityLists
                    .indexOfFirst { it == selected }
                    .takeIf { it != -1 }
                    ?: 0
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SelectorScreenModel(emptyList(), 0)
        )

    fun addList(list: CityListUi) {
        viewModelScope.launch {
            repository.insertList(list)
        }
    }

    fun onItemClick(item: CityListUi) {
        selected.value = item
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