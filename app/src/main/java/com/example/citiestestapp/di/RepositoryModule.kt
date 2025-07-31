package com.example.citiestestapp.di

import com.example.citiestestapp.data.database.CityListDao
import com.example.citiestestapp.data.repository.CityListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCityListRepository(dao: CityListDao): CityListRepository {
        return CityListRepository(dao)
    }
} 