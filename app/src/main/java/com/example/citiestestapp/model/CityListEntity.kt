package com.example.citiestestapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_lists")
data class CityListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val fullName: String,
    val color: Int,
    val cities: String
)