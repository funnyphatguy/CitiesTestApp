package com.example.citiestestapp.data

import kotlinx.coroutines.flow.Flow

class CityListRepository(private val dao: CityListDao) {
    fun getAllLists(): Flow<List<CityListEntity>> = dao.getAll()
    suspend fun insertList(list: CityListEntity) = dao.insert(list)

}