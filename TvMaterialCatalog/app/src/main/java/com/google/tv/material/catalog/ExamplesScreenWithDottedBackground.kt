package com.google.tv.material.catalog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExamplesScreenWithDottedBackground(
    actions: List<ExampleAction>
) {
    var activeAction by remember { mutableStateOf(actions[0]) }
    val firstItemFr = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        firstItemFr.requestFocus()
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 48.dp, end = 48.dp, bottom = 48.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.25f)
                .focusRestorer { firstItemFr }
                .focusGroup(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            actions.forEachIndexed { index, action ->
                key("list-item-$index") {
                    ListItem(
                        selected = activeAction == action,
                        onClick = { },
                        modifier = Modifier
                            .ifElse(index == 0, Modifier.focusRequester(firstItemFr))
                            .onFocusChanged {
                                if (it.isFocused) {
                                    activeAction = action
                                }
                            },
                        headlineContent = {
                            Text(text = action.title)
                        }
                    )
                }
            }
        }

        // dotted background
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            val color = MaterialTheme.colorScheme.border
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                val pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(
                        1.dp.toPx(),
                        size.height / 55
                    )
                )
                for (i in 0..size.height.toInt() step size.height.toInt() / 55) {
                    drawLine(
                        color = color,
                        Offset(0f, i.toFloat()),
                        Offset(size.width, i.toFloat()),
                        pathEffect = pathEffect
                    )
                }
            }
            activeAction.content()
        }
    }
}

data class ExampleAction(val title: String, val content: @Composable () -> Unit)
