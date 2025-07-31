package com.example.citiestestapp.di

import androidx.recyclerview.widget.LinearSnapHelper
import com.example.citiestestapp.model.CityUi
import com.example.citiestestapp.ui.newList.CityPreset
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CityModule {

    @Provides
    @Singleton
    fun provideAllCities(): List<CityUi> = CityPreset.entries.map { it.toCityUi() }

}