package com.example.citiestestapp.ui.selector

import androidx.recyclerview.widget.DiffUtil
import com.example.citiestestapp.model.CityListUi

class DiffSelectorUICallback(
    private val oldList: List<CityListUi>,
    private val newList: List<CityListUi>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos].id == newList[newPos].id

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        val oldItem = oldList[oldPos]
        val newItem = newList[newPos]
        return oldItem.shortName == newItem.shortName &&
                oldItem.fullName == newItem.fullName &&
                oldItem.color == newItem.color &&
                oldItem.cities == newItem.cities &&
                oldItem.isSelected == newItem.isSelected
    }

    override fun getChangePayload(oldPos: Int, newPos: Int): Any? {
        val oldItem = oldList[oldPos]
        val newItem = newList[newPos]
        return if (oldItem.isSelected != newItem.isSelected) {
            "PAYLOAD_SELECTION"
        } else null
    }
}
