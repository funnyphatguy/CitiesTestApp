package com.example.citiestestapp.di

import androidx.recyclerview.widget.LinearSnapHelper
import com.example.citiestestapp.ui.selector.BottomSheetConfigurator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object SelectorUiModule {

    @Provides
    fun provideBottomSheetConfigurator(): BottomSheetConfigurator = BottomSheetConfigurator()

    @Provides
    fun provideSnapHelper(): LinearSnapHelper = LinearSnapHelper()
}