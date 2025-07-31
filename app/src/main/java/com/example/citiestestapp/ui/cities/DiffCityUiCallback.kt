package com.example.citiestestapp.ui.cities

import androidx.recyclerview.widget.DiffUtil
import com.example.citiestestapp.model.CityUi

class DiffCityUiCallback(
    private val oldList: List<CityUi>,
    private val newList: List<CityUi>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean = oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean = oldList[oldItemPosition] == newList[newItemPosition]

}