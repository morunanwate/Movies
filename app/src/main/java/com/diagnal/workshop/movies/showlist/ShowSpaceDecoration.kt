package com.diagnal.workshop.movies.showlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ShowSpaceDecoration(private val horizontalSpacing: Int, private val verticalSpacing: Int) :
    RecyclerView.ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val totalSpanCount = getTotalSpanCount(parent)
        val spanSize = getItemSpanSize(parent, position)

        if (totalSpanCount == spanSize)
            return

        outRect.top = /*if (isInTheFirstRow(position, totalSpanCount)) 0 else */verticalSpacing
        outRect.left = getLeftSpacingForItem(position, totalSpanCount)
        outRect.right = getRightSpacingForItem(position, totalSpanCount)
        outRect.bottom = 0 // don't need
    }

    private fun isInTheFirstRow(position: Int, spanCount: Int): Boolean {
        return position < spanCount
    }

    private fun isFirstInRow(position: Int, spanCount: Int): Boolean {
        return position % spanCount == 0
    }

    private fun getTotalSpanCount(parent: RecyclerView): Int {
        val layoutManager = parent.layoutManager
        return if (layoutManager is GridLayoutManager) layoutManager.spanCount else 1
    }

    private fun getItemSpanSize(parent: RecyclerView, position: Int): Int {
        val layoutManager = parent.layoutManager
        return if (layoutManager is GridLayoutManager) layoutManager.spanSizeLookup.getSpanSize(
            position
        ) else 1
    }

    private fun getLeftSpacingForItem(position: Int, spanCount: Int): Int =
        (horizontalSpacing * getItemPositionInRow(position, spanCount)) / spanCount


    private fun getRightSpacingForItem(position: Int, spanCount: Int): Int =
        (horizontalSpacing * (spanCount - (getItemPositionInRow(position, spanCount) + 1))) / spanCount

    private fun getItemPositionInRow(position: Int, spanCount: Int) = position % spanCount

}