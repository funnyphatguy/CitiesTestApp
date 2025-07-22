package com.example.citiestestapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.data.City

class CityAdapter(
    val items: MutableList<City>

) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun swapItems(from: Int, to: Int) {
        if (from in items.indices && to in items.indices) {
            val temp = items[from]
            items[from] = items[to]
            items[to] = temp

            notifyItemMoved(from, to)
        }
    }

    fun updateItems(newItems: List<City>) {
        if (items.size != newItems.size || !items.containsAll(newItems)) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(city: City) {
            itemView.findViewById<TextView>(R.id.tvCityName).text = city.name
            itemView.findViewById<TextView>(R.id.tvCityYear).text = city.year
        }
    }
}