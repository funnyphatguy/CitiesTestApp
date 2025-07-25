package com.example.citiestestapp.data

import com.example.citiestestapp.model.CityListEntity
import kotlinx.coroutines.flow.Flow

class CityListRepository(private val dao: CityListDao) {
    fun getAllLists(): Flow<List<CityListEntity>> = dao.getAll()
    suspend fun insertList(list: CityListEntity) = dao.insert(list)

}