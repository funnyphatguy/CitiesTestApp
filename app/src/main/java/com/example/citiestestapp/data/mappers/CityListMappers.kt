package com.example.citiestestapp.data.mappers

import com.example.citiestestapp.model.City
import com.example.citiestestapp.model.CityList
import com.example.citiestestapp.model.CityListEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CityListMapper {

    private val gson by lazy { Gson() }

    private val cityListType by lazy {
        object : TypeToken<List<City>>() {}.type
    }

    fun entityToDomain(entity: CityListEntity): CityList {
        return CityList(
            id = entity.id,
            shortName = entity.name,
            fullName = entity.fullName,
            color = entity.color,
            cities = gson.fromJson(entity.cities, cityListType)
        )
    }

    fun domainToEntity(domain: CityList): CityListEntity {
        return CityListEntity(
            id = domain.id,
            name = domain.shortName,
            fullName = domain.fullName,
            color = domain.color,
            cities = gson.toJson(domain.cities)
        )
    }

    fun entityListToDomainList(entities: List<CityListEntity>): List<CityList> {
        return entities.map { entityToDomain(it) }
    }
}