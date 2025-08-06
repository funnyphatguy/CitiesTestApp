package com.example.citiestestapp.data.repository

import com.example.citiestestapp.data.database.CityListDao
import com.example.citiestestapp.model.CityListMapper.toEntity
import com.example.citiestestapp.model.CityListMapper.toUi
import com.example.citiestestapp.model.CityListUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityListRepository @Inject constructor(
    private val dao: CityListDao,
) {
    fun getAllLists(): Flow<List<CityListUi>> = dao
        .getAll()
        .flowOn(Dispatchers.Default)
        .map { entities ->
            entities.map { it.toUi() }
        }

    suspend fun insertList(list: CityListUi) = dao.insert(list.toEntity())
}