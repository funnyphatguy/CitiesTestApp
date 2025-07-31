package com.example.citiestestapp.data.repository

import com.example.citiestestapp.data.database.CityListDao
import com.example.citiestestapp.model.CityListMapper.toEntity
import com.example.citiestestapp.model.CityListMapper.toUi
import com.example.citiestestapp.model.CityListUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CityListRepository(
    private val dao: CityListDao,
) {

    fun getAllLists(): Flow<List<CityListUi>> = dao
        .getAll()
        .map { entities ->
            entities.map { it.toUi() }
        }

    suspend fun insertList(list: CityListUi) = dao.insert(list.toEntity())
}