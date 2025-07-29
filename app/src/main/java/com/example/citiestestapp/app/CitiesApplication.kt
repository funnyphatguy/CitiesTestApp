package com.example.citiestestapp.app

import android.app.Application
import androidx.room.Room
import com.example.citiestestapp.data.AppDatabase

class CitiesApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "cities_app_db"
        ).build()
    }
}