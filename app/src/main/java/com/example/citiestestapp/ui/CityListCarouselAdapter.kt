package com.example.citiestestapp.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.model.CityList

class CityListCarouselAdapter(
    var items: List<CityList>,
    var selectedIndex: Int = 0,
    private val onAddClick: () -> Unit = {},
    private val onItemClick: (CityList) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LIST = 0
        private const val TYPE_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < items.size) TYPE_LIST else TYPE_ADD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LIST) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city_list_carousel, parent, false)
            CityListViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city_list_add, parent, false)
            AddViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CityListViewHolder && position < items.size) {
            val isSelected = position == selectedIndex
            holder.bind(items[position], isSelected)
            holder.itemView.setOnClickListener { onItemClick(items[position]) }
        } else if (holder is AddViewHolder) {
            holder.bind()
            holder.itemView.setOnClickListener { onAddClick() }
        }
    }

    override fun getItemCount(): Int = items.size + 1 // +1 для кнопки +

    inner class CityListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cityList: CityList, isSelected: Boolean) {
            val tvShortName = itemView.findViewById<TextView>(R.id.tvShortName)
            tvShortName.text = cityList.shortName
            val bg = tvShortName.background as? GradientDrawable
            val colorInt = ContextCompat.getColor(itemView.context, cityList.color)
            bg?.setColor(colorInt)
            val scale = if (isSelected) 1.2f else 1.0f
            itemView.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val imageView = itemView.findViewById<android.widget.ImageView>(android.R.id.icon)
            if (imageView != null) {
                val bg = imageView.background as? GradientDrawable
                val yellowColor = ContextCompat.getColor(itemView.context, R.color.color_yellow)
                bg?.setColor(yellowColor)
            } else {
                // Если ImageView не найден по стандартному ID, ищем по layout
                val container = itemView as? android.widget.FrameLayout
                container?.let { frameLayout ->
                    for (i in 0 until frameLayout.childCount) {
                        val child = frameLayout.getChildAt(i)
                        if (child is android.widget.ImageView) {
                            val bg = child.background as? GradientDrawable
                            val yellowColor = ContextCompat.getColor(itemView.context, R.color.color_yellow)
                            bg?.setColor(yellowColor)
                            break
                        }
                    }
                }
            }
        }
    }
}