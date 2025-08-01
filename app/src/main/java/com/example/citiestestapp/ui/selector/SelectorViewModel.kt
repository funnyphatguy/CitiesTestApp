package com.example.citiestestapp.ui.selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiestestapp.data.repository.CityListRepository
import com.example.citiestestapp.model.CityListUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SelectorViewModel @Inject constructor(
    repository: CityListRepository
) : ViewModel() {
    val cityLists: Flow<List<CityListUi>> = repository.getAllLists()

    private val selected: MutableStateFlow<CityListUi?> = MutableStateFlow(null)

    val selectorScreenState: StateFlow<List<CityListUi>> =
        selected.combine(cityLists) { selectedItem, lists ->
            lists.map { ui ->
                ui.copy(isSelected = (ui.id == selectedItem?.id))
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    fun onItemClick(item: CityListUi) {
        selected.value = item
    }
}