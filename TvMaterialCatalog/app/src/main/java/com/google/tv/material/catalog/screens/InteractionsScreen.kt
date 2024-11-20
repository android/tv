package com.google.tv.material.catalog.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.Glow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground

@Composable
fun InteractionsScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Scale",
            content = {
                Surface(
                    onClick = { },
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(16f / 9),
                ) {}
            }
        ),
        ExampleAction(
            title = "Glow",
            content = {
                val glow = Glow(
                    elevation = 5.dp,
                    elevationColor = MaterialTheme.colorScheme.primary,
                )

                Surface(
                    onClick = { },
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(16f / 9),
                    scale = ClickableSurfaceScale.None,
                    glow = ClickableSurfaceDefaults.glow(
                        focusedGlow = glow,
                        pressedGlow = glow,
                    )
                ) {}
            }
        ),
        ExampleAction(
            title = "Border",
            content = {
                val border = Border(
                    border = BorderStroke(5.dp, MaterialTheme.colorScheme.border),
                )

                Surface(
                    onClick = { },
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(16f / 9),
                    scale = ClickableSurfaceScale.None,
                    border = ClickableSurfaceDefaults.border(
                        focusedBorder = border,
                        pressedBorder = border,
                    )
                ) {}
            }
        ),
        ExampleAction(
            title = "Inset",
            content = {
                val border = Border(
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.border),
                    inset = 4.dp,
                )

                Surface(
                    onClick = { },
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(16f / 9),
                    scale = ClickableSurfaceScale.None,
                    border = ClickableSurfaceDefaults.border(
                        focusedBorder = border,
                        pressedBorder = border,
                    )
                ) {}
            }
        ),
        ExampleAction(
            title = "Tonal elevation",
            content = {

                @Composable
                fun ElevationCard(elevation: Int) {
                    Surface(
                        onClick = { },
                        modifier = Modifier
                            .width(150.dp)
                            .aspectRatio(10f / 7),
                        scale = ClickableSurfaceScale.None,
                        tonalElevation = elevation.dp,
                        border = ClickableSurfaceDefaults.border(
                            border = Border(
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.border)
                            )
                        ),
                        shape = ClickableSurfaceDefaults.shape(
                            shape = RoundedCornerShape(15.dp)
                        ),
                    ) {
                        Text(
                            text = "Surface $elevation",
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 15.dp, bottom = 15.dp),
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        for (elevation in 0..2) {
                            ElevationCard(elevation = elevation)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        for (elevation in 3..5) {
                            ElevationCard(elevation = elevation)
                        }
                    }
                }
            }
        ),
        ExampleAction(
            title = "Disabled",
            content = {
                Surface(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(16f / 9),
                    scale = ClickableSurfaceScale.None,
                ) {}
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}