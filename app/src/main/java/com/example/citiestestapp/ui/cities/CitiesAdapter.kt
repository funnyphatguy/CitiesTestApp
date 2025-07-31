package com.example.citiestestapp.ui.cities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.databinding.ItemCityBinding
import com.example.citiestestapp.model.CityUi

class CitiesAdapter : RecyclerView.Adapter<CitiesAdapter.CityViewHolder>() {

    private val items = mutableListOf<CityUi>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityViewHolder = CityViewHolder(
        ItemCityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(newItems: List<CityUi>) {
        val diffUtilCallback = DiffCityUiCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CityViewHolder(
        private val binding: ItemCityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: CityUi) {
            binding.tvCityName.text = city.name
            binding.tvCityYear.text = city.year
        }
    }
}

