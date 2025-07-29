package com.example.citiestestapp.ui.cities

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.databinding.ItemCityBinding
import com.example.citiestestapp.model.City

class CitiesAdapter(
    val dataset: MutableList<City>

) : RecyclerView.Adapter<CitiesAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CityViewHolder {
        val binding = ItemCityBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return CityViewHolder(binding)
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    fun swapItems(from: Int, to: Int) {
        if (from in dataset.indices && to in dataset.indices) {
            val temp = dataset[from]
            dataset[from] = dataset[to]
            dataset[to] = temp

            notifyItemMoved(from, to)
        }
    }

    fun updateItems(newItems: List<City>) {
        Log.d("CityAdapter", "updateItems called with: $newItems")
        if (dataset.size != newItems.size || !dataset.containsAll(newItems)) {
            dataset.clear()
            dataset.addAll(newItems)
            Log.d(
                "CityAdapter", "Items updated, calling notifyDataSetChanged"
            )
            notifyDataSetChanged()
        } else {
            Log.d(
                "CityAdapter", "Items are the same, no update needed"
            )
        }
    }

    inner class CityViewHolder(
        private val binding: ItemCityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City) {
            binding.tvCityName.text = city.name
            binding.tvCityYear.text = city.year
        }
    }
}