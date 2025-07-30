package com.example.citiestestapp.ui.selector

import com.example.citiestestapp.model.CityListUi

data class SelectorScreenModel(
    val cities: List<CityListUi>,
    val selectedItem: Int = 0
)