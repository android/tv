package com.google.tv.material.catalog.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedButtonDefaults
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.OutlinedIconButtonDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground

@Composable
fun ButtonsScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Button",
            content = {
                Row {
                    Button(onClick = { }) {
                        Text(text = "Favourite")
                    }

                    Spacer(Modifier.size(20.dp))

                    Button(
                        onClick = { },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Favourite")
                    }
                }
            }
        ),
        ExampleAction(
            title = "Outlined button",
            content = {
                Row {
                    OutlinedButton(onClick = { }) {
                        Text(text = "Favourite")
                    }

                    Spacer(Modifier.size(20.dp))

                    OutlinedButton(
                        onClick = { },
                        contentPadding = OutlinedButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(OutlinedButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(OutlinedButtonDefaults.IconSpacing))
                        Text(text = "Favourite")
                    }
                }
            }
        ),
        ExampleAction(
            title = "Icon buttons",
            content = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(IconButtonDefaults.SmallButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.SmallIconSize)
                            )
                        }

                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(IconButtonDefaults.MediumButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.MediumIconSize)
                            )
                        }

                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(IconButtonDefaults.LargeButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.LargeIconSize)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        OutlinedIconButton(
                            onClick = { },
                            modifier = Modifier.size(OutlinedIconButtonDefaults.SmallButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(OutlinedIconButtonDefaults.SmallIconSize)
                            )
                        }

                        OutlinedIconButton(
                            onClick = { },
                            modifier = Modifier.size(OutlinedIconButtonDefaults.MediumButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(OutlinedIconButtonDefaults.MediumIconSize)
                            )
                        }

                        OutlinedIconButton(
                            onClick = { },
                            modifier = Modifier.size(OutlinedIconButtonDefaults.LargeButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(OutlinedIconButtonDefaults.LargeIconSize)
                            )
                        }
                    }
                }
            }
        ),
        ExampleAction(
            title = "Wide button",
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    WideButton(
                        onClick = { },
                        title = { Text("Settings") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    )

                    WideButton(
                        onClick = { },
                        title = { Text("Settings") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        subtitle = { Text(text = "Update device preferences") },
                    )
                }
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}