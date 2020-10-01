package com.nickilanjelo.imagepickerapp.decoration

import android.R.attr.spacing
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EdgeItemDecoration(
        private val offset: Int,
        private val orientation: Int = VERTICAL
) : RecyclerView.ItemDecoration() {

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
        const val GRID = 3
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val itemPosition = parent.getChildAdapterPosition(view)

        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (orientation == GRID) {
            val position = parent.getChildAdapterPosition(view)

            val totalSpanCount = getTotalSpanCount(parent)
            val spanSize = getItemSpanSize(parent, position)
            if (totalSpanCount == spanSize) {
                return
            }

            outRect.top = if (isInTheFirstRow(position, totalSpanCount)) 0 else offset
            outRect.left = if (isFirstInRow(position, totalSpanCount)) 0 else offset / 8
            outRect.right = if (isLastInRow(position, totalSpanCount)) 0 else offset / 8
            outRect.bottom = 0
        } else {
            val itemCount = state.itemCount

            if (itemPosition == itemCount - 1) {
                when (orientation) {
                    VERTICAL -> outRect.bottom = offset
                    HORIZONTAL -> outRect.right = offset
                }
            }
            else {
                when (orientation) {
                    VERTICAL -> outRect.bottom = offset / 8
                    HORIZONTAL -> outRect.right = offset / 8
                }
            }
        }
    }

    private fun isInTheFirstRow(position: Int, spanCount: Int): Boolean {
        return position < spanCount
    }

    private fun isFirstInRow(position: Int, spanCount: Int): Boolean {
        return position % spanCount == 0
    }

    private fun isLastInRow(position: Int, spanCount: Int): Boolean {
        return isFirstInRow(position + 1, spanCount)
    }

    private fun getTotalSpanCount(parent: RecyclerView): Int {
        val layoutManager = parent.layoutManager
        return if (layoutManager is GridLayoutManager) layoutManager.spanCount else 1
    }

    private fun getItemSpanSize(parent: RecyclerView, position: Int): Int {
        val layoutManager = parent.layoutManager
        return if (layoutManager is GridLayoutManager) layoutManager.spanSizeLookup.getSpanSize(position) else 1
    }
}