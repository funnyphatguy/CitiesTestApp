package com.example.citiestestapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.citiestestapp.model.CityListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityListDao {
    @Query("SELECT * FROM city_lists")
    fun getAll(): Flow<List<CityListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: CityListEntity)

}