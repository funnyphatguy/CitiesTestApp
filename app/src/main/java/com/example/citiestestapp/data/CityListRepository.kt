package com.example.citiestestapp.data

import com.example.citiestestapp.data.mappers.CityListMapper
import com.example.citiestestapp.model.CityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CityListRepository(
    private val dao: CityListDao,
    private val mapper: CityListMapper = CityListMapper()
) {

    fun getAllLists(): Flow<List<CityList>> =
        dao.getAll().map { entities ->
            mapper.entityListToDomainList(entities)
        }

    suspend fun insertList(list: CityList) =
        dao.insert(mapper.domainToEntity(list))
}