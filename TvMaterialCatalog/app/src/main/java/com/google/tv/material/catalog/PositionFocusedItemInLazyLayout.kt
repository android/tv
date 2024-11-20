package com.google.tv.material.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PositionFocusedItemInLazyLayout(
    parentFraction: Float = 0.3f,
    childFraction: Float = 0f,
    content: @Composable () -> Unit,
) {
    // This bring-into-view spec pivots around the center of the scrollable container
    val bringIntoViewSpec = object : BringIntoViewSpec {
        override fun calculateScrollDistance(
            // Initial position of item requesting focus
            offset: Float,
            // Size of item requesting focus
            size: Float,
            // Size of the lazy container
            containerSize: Float
        ): Float {
            val childSmallerThanParent = size <= containerSize
            val initialTargetForLeadingEdge =
                parentFraction * containerSize - (childFraction * size)
            val spaceAvailableToShowItem = containerSize - initialTargetForLeadingEdge

            val targetForLeadingEdge =
                if (childSmallerThanParent && spaceAvailableToShowItem < size) {
                    containerSize - size
                } else {
                    initialTargetForLeadingEdge
                }

            return offset - targetForLeadingEdge
        }
    }

    // LocalBringIntoViewSpec will apply to all scrollables in the hierarchy
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}
