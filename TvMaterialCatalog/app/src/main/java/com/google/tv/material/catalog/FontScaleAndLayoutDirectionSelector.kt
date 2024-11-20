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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
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
fun FontScaleAndLayoutDirectionSelector(
    isExpanded: Boolean,
    onClose: () -> Unit,
    onLayoutDirectionChange: (LayoutDirection) -> Unit,
    onFontScaleChange: (scale: Float) -> Unit,
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
                                text = "Font scale",
                                modifier = Modifier.padding(20.dp),
                                fontSize = 20.sp
                            )
                        }

                        item {
                            Column {
                                Text(
                                    text = "TEXT DIRECTION",
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                    fontSize = 12.sp
                                )

                                Column(
                                    Modifier.selectableGroup()
                                ) {
                                    layoutDirections.forEach {
                                        val isSelected =
                                            it.direction == LocalLayoutDirection.current

                                        ListItem(
                                            selected = isSelected,
                                            onClick = { onLayoutDirectionChange(it.direction) },
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
                                    text = "FONT SCALE",
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                    fontSize = 12.sp
                                )

                                Column(
                                    Modifier.selectableGroup()
                                ) {
                                    fontScales.forEach {
                                        val isSelected =
                                            LocalDensity.current.fontScale == it.scale
                                        ListItem(
                                            selected = isSelected,
                                            onClick = { onFontScaleChange(it.scale) },
                                            modifier = Modifier.semantics(mergeDescendants = true) { },
                                            leadingContent = {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_font_scale),
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
                    }
                }
            }
        }
    }
}

private data class LayoutDirectionConfig(
    val direction: LayoutDirection,
    val title: String,
    val icon: Int
)

private val layoutDirections = listOf(
    LayoutDirectionConfig(
        direction = LayoutDirection.Ltr,
        title = "LTR",
        icon = R.drawable.ic_ltr,
    ),
    LayoutDirectionConfig(
        direction = LayoutDirection.Rtl,
        title = "RTL",
        icon = R.drawable.ic_rtl,
    )
)
