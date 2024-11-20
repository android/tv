package com.google.tv.material.catalog.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CompactCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.google.tv.material.catalog.PositionFocusedItemInLazyLayout
import com.google.tv.material.catalog.R
import com.google.tv.material.catalog.ifElse

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImmersiveListScreen() {
    var selectedCard by remember { mutableStateOf(immersiveListItems.first()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // background image
        Image(
            painter = painterResource(id = selectedCard.image),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // gradient and text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .immersiveListGradient(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 58.dp, bottom = 16.dp)
                    .width(480.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = selectedCard.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                )

                Text(
                    text = selectedCard.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = selectedCard.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                )
            }
        }

        val density = LocalDensity.current
        var fullWidth by remember { mutableFloatStateOf(1f) }
        val firstChildFr = remember { FocusRequester() }

        PositionFocusedItemInLazyLayout(
            parentFraction = 58f / fullWidth,
            childFraction = 0f
        ) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .focusRestorer { firstChildFr }
                    .onPlaced {
                        with(density) {
                            fullWidth = it.size.width.toDp().value
                        }
                    },
                contentPadding = PaddingValues(start = 58.dp, end = 46.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(immersiveListItems) { index, card ->
                    CompactCard(
                        modifier = Modifier
                            .width(280.dp)
                            .aspectRatio(16f / 9)
                            .ifElse(index == 0, Modifier.focusRequester(firstChildFr))
                            .onFocusChanged {
                                if (it.isFocused) {
                                    selectedCard = card
                                }
                            },
                        onClick = {},
                        image = {
                            Image(
                                painter = painterResource(id = card.image),
                                contentDescription = "Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )
                        },
                        title = {},
                        colors = CardDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

private val description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi" +
        " ut aliquip ex ea commodo consequat. "

private val immersiveListItems = listOf(
    ImmersiveListSlide(
        title = "Shadow Hunter",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_1,
    ),
    ImmersiveListSlide(
        title = "Super Puppy",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_2,
    ),
    ImmersiveListSlide(
        title = "Man with a cape",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_3,
    ),
    ImmersiveListSlide(
        title = "Power Sisters",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_4,
    ),
)

private data class ImmersiveListSlide(
    val title: String,
    val subtitle: String,
    val description: String,
    val image: Int = 10,
)

fun Modifier.immersiveListGradient(): Modifier = composed {
    val color = MaterialTheme.colorScheme.surface

    val colorAlphaList = listOf(1.0f, 0.2f, 0.0f)
    val colorStopList = listOf(0.2f, 0.8f, 0.9f)

    val colorAlphaList2 = listOf(1.0f, 0.1f, 0.0f)
    val colorStopList2 = listOf(0.1f, 0.4f, 0.9f)
    this
        .then(
            background(
                brush = Brush.linearGradient(
                    colorStopList[0] to color.copy(alpha = colorAlphaList[0]),
                    colorStopList[1] to color.copy(alpha = colorAlphaList[1]),
                    colorStopList[2] to color.copy(alpha = colorAlphaList[2]),
                    start = Offset(0.0f, 0.0f),
                    end = Offset(Float.POSITIVE_INFINITY, 0.0f)
                )
            )
        )
        .then(
            background(
                brush = Brush.linearGradient(
                    colorStopList2[0] to color.copy(alpha = colorAlphaList2[0]),
                    colorStopList2[1] to color.copy(alpha = colorAlphaList2[1]),
                    colorStopList2[2] to color.copy(alpha = colorAlphaList2[2]),
                    start = Offset(0f, Float.POSITIVE_INFINITY),
                    end = Offset(0f, 0f)
                )
            )
        )
}
