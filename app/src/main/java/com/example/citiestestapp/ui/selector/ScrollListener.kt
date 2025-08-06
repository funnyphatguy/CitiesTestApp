package com.example.citiestestapp.ui.selector

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.citiestestapp.model.CityListUi

class ScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val snapHelper: SnapHelper,
    private val adapter: ListSelectorAdapter,
    private val recyclerView: RecyclerView,
    private val onItemSelected: (CityListUi) -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            handleIdleState()
        }
    }

     private fun handleIdleState() {
        val centeredView = snapHelper.findSnapView(layoutManager) ?: return
        val centeredPosition = layoutManager.getPosition(centeredView)
        val isAddButton = centeredPosition >= adapter.items.size

        if (isAddButton) {
            val selectedIndex = adapter.items.indexOfFirst { it.isSelected }
            val target = selectedIndex.coerceIn(0, adapter.items.lastIndex)
            recyclerView.smoothScrollToPosition(target)
        } else {
            val selectedItem = adapter.items[centeredPosition]
            val currentSelected = adapter.items.find { it.isSelected }

            if (currentSelected?.id != selectedItem.id) {
                val updatedItems = adapter.items.map { item ->
                    item.copy(isSelected = item.id == selectedItem.id)
                }

                adapter.updateItems(updatedItems)
                onItemSelected(selectedItem)
            }
        }
    }
}

