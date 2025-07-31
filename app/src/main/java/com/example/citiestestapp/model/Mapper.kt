package com.example.citiestestapp.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CityListMapper {

    val gson by lazy { Gson() }

    private val cityListType by lazy {
        object : TypeToken<List<CityUi>>() {}.type
    }

    fun CityListEntity.toUi(): CityListUi = with(this) {
        CityListUi(
            id = id,
            shortName = name,
            fullName = fullName,
            color = color,
            cities = gson.fromJson(cities, cityListType)
        )
    }

    fun CityListUi.toEntity(): CityListEntity = with(this) {
        CityListEntity(
            id = id,
            name = shortName,
            fullName = fullName,
            color = color,
            cities = gson.toJson(cities)
        )
    }
}