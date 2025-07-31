package com.example.citiestestapp.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class CityListUi(
    val id: String = UUID.randomUUID().toString(),
    val shortName: String,
    val fullName: String,
    @field:ColorRes val color: Int,
    val cities: List<CityUi>,
    val isSelected: Boolean = false
) : Parcelable