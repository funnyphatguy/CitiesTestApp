package com.example.citiestestapp.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CenterItemDecoration(private val itemWidth: Int, private val recyclerViewWidth: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        val sidePadding = (recyclerViewWidth - itemWidth) / 2
        if (position == 0) {
            outRect.left = sidePadding
        }
        if (position == itemCount - 1) {
            outRect.right = sidePadding
        }
    }
}