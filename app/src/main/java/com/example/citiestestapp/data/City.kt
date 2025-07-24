package com.example.citiestestapp.data

import androidx.annotation.ColorRes
import java.util.UUID

data class City(
    val name: String,
    val year: String
)

data class CityList(
    val id: String = UUID.randomUUID().toString(),
    val shortName: String,
    val fullName: String,
    @field:ColorRes val color: Int,
    val cities: List<City>
)
