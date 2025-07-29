package com.example.citiestestapp.ui.selector

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.ItemCityListAddBinding
import com.example.citiestestapp.databinding.ItemCityListCarouselBinding
import com.example.citiestestapp.model.CityList

class ListSelectorAdapter(
    var dataset: List<CityList>,
    var selectedIndex: Int = 0,
    private val onAddClick: () -> Unit = {},
    private val onItemClick: (CityList) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return if (position < dataset.size) TYPE_LIST else TYPE_ADD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LIST) {
            val binding = ItemCityListCarouselBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CityListViewHolder(binding)
        } else {
            val binding =
                ItemCityListAddBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            AddViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CityListViewHolder && position < dataset.size) {
            val isSelected = position == selectedIndex
            holder.bind(dataset[position], isSelected)
            holder.itemView.setOnClickListener { onItemClick(dataset[position]) }
        } else if (holder is AddViewHolder) {
            holder.bind()
            holder.itemView.setOnClickListener { onAddClick() }
        }
    }

    override fun getItemCount(): Int = dataset.size + 1 // +1 для кнопки +

    inner class CityListViewHolder(private val binding: ItemCityListCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cityList: CityList, isSelected: Boolean) {
            binding.tvShortName.text = cityList.shortName
            val bg = binding.tvShortName.background as? GradientDrawable
            val colorInt = ContextCompat.getColor(itemView.context, cityList.color)
            bg?.setColor(colorInt)
            val scale = if (isSelected) 1.2f else 1.0f
            itemView.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    inner class AddViewHolder(private val binding: ItemCityListAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val bg = binding.ivAdd.background as? GradientDrawable
            val yellowColor = ContextCompat.getColor(itemView.context, R.color.color_yellow)
            bg?.setColor(yellowColor)
        }
    }

    companion object {
        private const val TYPE_LIST = 0
        private const val TYPE_ADD = 1
    }

}