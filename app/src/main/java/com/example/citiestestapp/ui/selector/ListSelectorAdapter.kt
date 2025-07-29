package com.example.citiestestapp.ui.selector

import android.graphics.drawable.GradientDrawable
import android.util.Log
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

    override fun getItemViewType(position: Int): Int =
        if (position < dataset.size) TYPE_LIST else TYPE_ADD

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        if (viewType == TYPE_LIST) CityListViewHolder(
            ItemCityListCarouselBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        ) else AddViewHolder(
            ItemCityListAddBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onAddClick
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CityListViewHolder -> {
                if (position >= dataset.size) {
                    Log.w("ListSelectorAdapter", "Invalid position: $position")
                    return
                }
                val isSelected = position == selectedIndex
                holder.bind(dataset[position], isSelected)
            }

            is AddViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = dataset.size + 1

    inner class CityListViewHolder(
        private val binding: ItemCityListCarouselBinding,
        private val onClick: (CityList) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentCityList: CityList? = null

        init {
            itemView.setOnClickListener {
                currentCityList?.let { onClick(it) }
            }
        }

        fun bind(cityList: CityList, isSelected: Boolean) {
            currentCityList = cityList
            binding.tvShortName.text = cityList.shortName
            (binding.tvShortName.background as? GradientDrawable)?.setColor(
                ContextCompat.getColor(itemView.context, cityList.color)
            )
            val scale = if (isSelected) 1.2f else 1.0f
            itemView.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    inner class AddViewHolder(
        private val binding: ItemCityListAddBinding,
        private val onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onClick() }
        }

        fun bind() {
            (binding.ivAdd.background as? GradientDrawable)?.setColor(
                ContextCompat.getColor(itemView.context, R.color.color_yellow)
            )
        }
    }

    companion object {
        private const val TYPE_LIST = 0
        private const val TYPE_ADD = 1
    }
}