package com.example.citiestestapp.di

import android.content.Context
import com.example.citiestestapp.data.database.AppDatabase
import com.example.citiestestapp.data.database.CityListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.create(context)
    }

    @Provides
    fun provideCityListDao(database: AppDatabase): CityListDao {
        return database.cityListDao()
    }
} 