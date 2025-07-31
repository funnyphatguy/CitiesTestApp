package com.example.citiestestapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityUi(
    val name: String,
    val year: String
) : Parcelable
