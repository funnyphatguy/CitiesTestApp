package com.example.citiestestapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.model.City

class CityAdapter(
    val dataset: MutableList<City>

) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
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
        android.util.Log.d("CityAdapter", "updateItems called with: $newItems")
        if (dataset.size != newItems.size || !dataset.containsAll(newItems)) {
            dataset.clear()
            dataset.addAll(newItems)
            android.util.Log.d("CityAdapter", "Items updated, calling notifyDataSetChanged")
            notifyDataSetChanged()
        } else {
            android.util.Log.d("CityAdapter", "Items are the same, no update needed")
        }
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(city: City) {
            itemView.findViewById<TextView>(R.id.tvCityName).text = city.name
            itemView.findViewById<TextView>(R.id.tvCityYear).text = city.year
        }
    }
}