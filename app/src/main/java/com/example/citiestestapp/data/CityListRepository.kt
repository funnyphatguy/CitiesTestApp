package com.example.citiestestapp.data

import kotlinx.coroutines.flow.Flow
import com.example.citiestestapp.model.CityListEntity

class CityListRepository(private val dao: CityListDao) {
    fun getAllLists(): Flow<List<CityListEntity>> = dao.getAll()
    suspend fun insertList(list: CityListEntity) = dao.insert(list)

}