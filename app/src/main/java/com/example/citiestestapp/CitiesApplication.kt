package com.example.citiestestapp

import android.app.Application
import com.example.citiestestapp.data.database.AppDatabase

class CitiesApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.create(this) }
}