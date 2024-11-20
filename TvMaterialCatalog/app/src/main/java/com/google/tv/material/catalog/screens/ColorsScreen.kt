package com.google.tv.material.catalog.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text

@Composable
fun ColorsScreen() {
    Row(
        Modifier.fillMaxSize().padding(start = 58.dp, end = 48.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(2) }) {
                ColorBg(text = "On Background", color = MaterialTheme.colorScheme.onBackground)
            }
            item(span = { GridItemSpan(2) }) {
                ColorBg(text = "Background", color = MaterialTheme.colorScheme.background)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "On Surface", color = MaterialTheme.colorScheme.onSurface)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "On Surface Variant",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item(span = { GridItemSpan(2) }) {
                ColorBg(text = "Surface", color = MaterialTheme.colorScheme.surface)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Inverse Primary", color = MaterialTheme.colorScheme.inversePrimary)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "Inverse On Surface",
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Surface Variant", color = MaterialTheme.colorScheme.surfaceVariant)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Inverse Surface", color = MaterialTheme.colorScheme.inverseSurface)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "On Primary Container",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "On Primary", color = MaterialTheme.colorScheme.onPrimary)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "Primary Container",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Primary", color = MaterialTheme.colorScheme.primary)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "On Secondary Container",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "On Secondary", color = MaterialTheme.colorScheme.onSecondary)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Secondary", color = MaterialTheme.colorScheme.secondaryContainer)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Secondary", color = MaterialTheme.colorScheme.secondary)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "On Tertiary Container",
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "On Tertiary", color = MaterialTheme.colorScheme.onTertiary)
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(
                    text = "Tertiary Container",
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
            item(span = { GridItemSpan(1) }) {
                ColorBg(text = "Tertiary", color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun ColorBg(
    text: String,
    color: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = SurfaceDefaults.colors(
            containerColor = color
        ),
        border = Border(
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.border)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                if (color.luminance() < .25) Color.White else Color.Black
            )
        )
    }
}
