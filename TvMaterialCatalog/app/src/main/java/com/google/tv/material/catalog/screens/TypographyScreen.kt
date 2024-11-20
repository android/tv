package com.google.tv.material.catalog.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground

@Composable
fun TypographyScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Display",
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Text(
                        text = "Display Large",
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Text(
                        text = "Display Medium",
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = "Display Small",
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            }
        ),
        ExampleAction(
            title = "Headline",
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Headline Large",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text(
                        text = "Headline Medium",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = "Headline Small",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
        ),
        ExampleAction(
            title = "Title",
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Title Large",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Title Medium",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Title Small",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        ),
        ExampleAction(
            title = "Label",
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Label Large",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = "Label Medium",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "Label Small",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        ),
        ExampleAction(
            title = "Body",
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Body Large",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Body Medium",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Body Small",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}