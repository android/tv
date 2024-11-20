package com.google.tv.material.catalog.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DenseListItem
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.Text
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground

@Composable
fun ListsScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Default list",
            content = {
                Column(
                    Modifier.width(300.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    var selected1 by remember { mutableStateOf(false) }
                    ListItem(
                        selected = selected1,
                        onClick = { selected1 = !selected1 },
                        headlineContent = { Text("Favourites") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(ListItemDefaults.IconSize)
                            )
                        },
                    )

                    var selected2 by remember { mutableStateOf(false) }
                    ListItem(
                        selected = selected2,
                        onClick = { selected2 = !selected2 },
                        headlineContent = {
                            Text(
                                text = "Favourites",
                            )
                                          },
                        supportingContent = {
                            Text(
                                text = "You like this",
                                modifier = Modifier.padding(top = 4.dp)
                            )
                                            },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(ListItemDefaults.IconSize)
                            )
                        },
                    )
                }
            }
        ),
        ExampleAction(
            title = "Dense list",
            content = {
                Column(
                    Modifier.width(300.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    var selected1 by remember { mutableStateOf(false) }
                    DenseListItem(
                        selected = selected1,
                        onClick = { selected1 = !selected1 },
                        headlineContent = { Text("Favourites") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(ListItemDefaults.IconSizeDense)
                            )
                        },
                    )

                    var selected2 by remember { mutableStateOf(false) }
                    DenseListItem(
                        selected = selected2,
                        onClick = { selected2 = !selected2 },
                        headlineContent = {
                            Text(
                                text = "Favourites",
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "You like this",
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(ListItemDefaults.IconSizeDense)
                            )
                        },
                    )
                }
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}
