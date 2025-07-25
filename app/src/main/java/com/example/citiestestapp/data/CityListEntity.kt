package com.example.citiestestapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.citiestestapp.data.City
import com.example.citiestestapp.data.CityList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "city_lists")
data class CityListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val fullName: String,
    val color: Int,
    val cities: String
)

fun CityListEntity.toDomain(): CityList {
    val gson = Gson()
    val cityType = object : TypeToken<List<City>>() {}.type
    return CityList(
        id = id,
        shortName = name,
        fullName = fullName,
        color = color,
        cities = gson.fromJson(cities, cityType)
    )
}

fun CityList.toEntity(): CityListEntity {
    val gson = Gson()
    return CityListEntity(
        id = id,
        name = shortName,
        fullName = fullName,
        color = color,
        cities = gson.toJson(cities)
    )
}