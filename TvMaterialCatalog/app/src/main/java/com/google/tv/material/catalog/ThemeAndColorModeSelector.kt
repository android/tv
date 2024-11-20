package com.google.tv.material.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ThemeAndColorModeSelector(
    isExpanded: Boolean,
    onClose: () -> Unit,
    onSeedColorChange: (SeedColor) -> Unit,
    onThemeModeChange: (Mode) -> Unit,
    content: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    BackHandler(enabled = isExpanded) {
        onClose()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
            ) {
                Surface(
                    Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .padding(12.dp)
                        .align(Alignment.End),
                    colors = SurfaceDefaults
                        .colors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(12.dp)
                            .focusRequester(focusRequester)
                            .focusProperties {
                                exit = {
                                    onClose()
                                    FocusRequester.Default
                                }
                            }
                    ) {
                        item {
                            Text(
                                text = "Theme & color mode",
                                modifier = Modifier.padding(20.dp),
                                fontSize = 20.sp
                            )
                        }

                        item {
                            Column {
                                Text(
                                    text = "THEME",
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                    fontSize = 12.sp
                                )

                                Column(
                                    Modifier.selectableGroup()
                                ) {
                                    themeModes.forEach {
                                        val isSelected = it.mode == LocalMode.current

                                        ListItem(
                                            selected = isSelected,
                                            onClick = { onThemeModeChange(it.mode) },
                                            modifier = Modifier.semantics(mergeDescendants = true) { },
                                            leadingContent = {
                                                Icon(
                                                    painter = painterResource(id = it.icon),
                                                    contentDescription = null
                                                )
                                            },
                                            trailingContent = {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { })
                                            },
                                            headlineContent = {
                                                Text(text = it.title)
                                            })
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        item {
                            Column {
                                Text(
                                    text = "COLOR MODE",
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                    fontSize = 12.sp
                                )

                                Column(
                                    Modifier.selectableGroup()
                                ) {
                                    seedColors.forEach {
                                        val isSelected =
                                            LocalThemeSeedColor.current.name == it.name
                                        ListItem(
                                            selected = isSelected,
                                            onClick = { onSeedColorChange(it) },
                                            modifier = Modifier.semantics(mergeDescendants = true) { },
                                            leadingContent = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(15.dp)
                                                        .background(it.color, CircleShape)
                                                )
                                            },
                                            trailingContent = {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { })
                                            },
                                            headlineContent = {
                                                Text(text = it.name)
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
