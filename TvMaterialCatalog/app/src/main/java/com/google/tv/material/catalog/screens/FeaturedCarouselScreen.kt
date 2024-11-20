package com.google.tv.material.catalog.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Carousel
import androidx.tv.material3.CarouselDefaults
import androidx.tv.material3.CarouselState
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.google.tv.material.catalog.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedCarouselScreen() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        val carouselState = remember { CarouselState(0) }
        Carousel(
            itemCount = slides.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            carouselState = carouselState,
            carouselIndicator = {
                CarouselDefaults.IndicatorRow(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 56.dp, end = 58.dp),
                    activeItemIndex = carouselState.activeItemIndex,
                    itemCount = slides.size
                )
            },
        ) { index ->
            val slide = slides[index]

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // image
                Image(
                    painter = painterResource(id = slide.image),
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth,
                )

                // gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .carouselGradient()
                )

                // text content
                Box(
                    modifier = Modifier
                        .padding(start = 58.dp, bottom = 52.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .width(480.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = slide.subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                        )

                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(
                            modifier = Modifier
                                .height(20.dp)
                        )

                        Text(
                            text = slide.description,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                        )

                        Spacer(
                            modifier = Modifier
                                .height(24.dp)
                        )

                        Button(
                            onClick = {},
                            modifier = Modifier,
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = "Watch on YouTube",
                                style = MaterialTheme.typography.labelLarge.copy(textMotion = TextMotion.Animated)
                            )
                        }
                    }
                }
            }
        }

        BottomCards(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Modifier.onFirstGainingVisibility(onGainingVisibility: () -> Unit): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) {
        if (isVisible) onGainingVisibility()
    }

    return onPlaced { isVisible = true }
}

@Composable
private fun Modifier.requestFocusOnFirstGainingVisibility(): Modifier {
    val focusRequester = remember { FocusRequester() }
    return focusRequester(focusRequester)
        .onFirstGainingVisibility { focusRequester.requestFocus() }
}

@OptIn(ExperimentalTvMaterial3Api::class)
fun Modifier.carouselGradient(): Modifier = composed {
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

@Composable
fun BottomCards(modifier: Modifier) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 36.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(5) {
            Surface(
                onClick = { /*TODO*/ },
                modifier = Modifier.width(268.dp).aspectRatio(16f / 9),
                shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(20.dp)),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f)
                ),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.border
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                )
            ) {

            }
        }
    }
}


private val description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi" +
        " ut aliquip ex ea commodo consequat. "

private val slides = listOf(
    Slide(
        title = "Shadow Hunter",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_1,
    ),
    Slide(
        title = "Super Puppy",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_2,
    ),
    Slide(
        title = "Man with a cape",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_3,
    ),
    Slide(
        title = "Power Sisters",
        subtitle = "Secondary · text",
        description = description,
        image = R.drawable.fc_4,
    ),
)

private data class Slide(
    val title: String,
    val subtitle: String,
    val description: String,
    val image: Int = 10,
)
