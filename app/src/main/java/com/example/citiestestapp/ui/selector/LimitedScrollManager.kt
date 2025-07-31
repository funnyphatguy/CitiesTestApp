package com.example.citiestestapp.ui.selector

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class LimitedScrollManager(
    context: Context,
    private val maxIndex: () -> Int
) : LinearLayoutManager(context, HORIZONTAL, false) {

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val lastVisible = findLastVisibleItemPosition()
        return if (dx > 0 && lastVisible >= maxIndex()) {
            super.scrollHorizontallyBy((dx * 0.3f).toInt(), recycler, state)
        } else {
            super.scrollHorizontallyBy(dx, recycler, state)
        }
    }
}
