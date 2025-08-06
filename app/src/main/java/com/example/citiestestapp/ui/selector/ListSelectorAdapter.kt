package com.example.citiestestapp.ui.selector

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.citiestestapp.R
import com.example.citiestestapp.databinding.ItemCityListAddBinding
import com.example.citiestestapp.databinding.ItemCityListCarouselBinding
import com.example.citiestestapp.model.CityListUi


class ListSelectorAdapter(
    private val onAddClick: () -> Unit = {},
    private val onItemClick: (CityListUi) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val items = mutableListOf<CityListUi>()

    class CityListViewHolder(
        private val binding: ItemCityListCarouselBinding,
        private val onClick: (CityListUi) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cityList: CityListUi, isSelected: Boolean) {
            itemView.setOnClickListener { onClick(cityList) }
            binding.shortNameTextView.text = cityList.name
            (binding.shortNameTextView.background as? GradientDrawable)
                ?.setColor(ContextCompat.getColor(itemView.context, cityList.color))
            val scale = if (isSelected) 1.2f else 1.0f
            itemView.scaleX = scale
            itemView.scaleY = scale
        }

        fun updateSelection(isSelected: Boolean) {
            val targetScale = if (isSelected) 1.2f else 1.0f
            itemView.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(200)
                .start()
        }
    }

    class AddViewHolder(
        private val binding: ItemCityListAddBinding,
        onClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener { onClick() }
        }
        fun bind() {
            (binding.addImageView.background as? GradientDrawable)
                ?.setColor(ContextCompat.getColor(itemView.context, R.color.yellow))
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position < items.size) TYPE_LIST else TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_LIST) {
            CityListViewHolder(
                ItemCityListCarouselBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClick
            )
        } else {
            AddViewHolder(
                ItemCityListAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onAddClick
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (holder) {
            is CityListViewHolder -> {
                val cityList = items[position]
                if (payloads.contains(PAYLOAD_SELECTION)) {
                    holder.updateSelection(cityList.isSelected)
                } else {
                    holder.bind(cityList, cityList.isSelected)
                }
            }
            is AddViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = items.size + 1

    fun updateItems(newItems: List<CityListUi>) {
        val diffResult = DiffUtil.calculateDiff(DiffSelectorUICallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)

    }

    companion object {
        private const val TYPE_LIST = 0
        private const val TYPE_ADD = 1
        private const val PAYLOAD_SELECTION = "SELECTION_CHANGED"
    }
}
