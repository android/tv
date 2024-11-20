package com.google.tv.material.catalog.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.CompactCard
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import androidx.tv.material3.WideCardContainer
import androidx.tv.material3.WideClassicCard
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground
import com.google.tv.material.catalog.R

@Composable
fun CardsScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Standard card",
            content = {
                StandardCardContainer(
                    modifier = Modifier.width(180.dp),
                    imageCard = {
                        Card(
                            onClick = {},
                            interactionSource = it,
                            colors = CardDefaults.colors(containerColor = Color.Transparent)
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                                painter = painterResource(id = R.drawable.card_thumbnail),
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Title goes here",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    },
                    subtitle = {
                        Text(text = "Secondary · text")
                    },
                )
            }
        ),
        ExampleAction(
            title = "Classic card",
            content = {
                ClassicCard(
                    onClick = { },
                    modifier = Modifier.width(180.dp),
                    image = {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                            painter = painterResource(id = R.drawable.card_thumbnail),
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(
                            text = "Title goes here",
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        )
                    },
                    subtitle = {
                        Text(
                            text = "Secondary · text",
                            modifier = Modifier.padding(
                                top = 4.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                        )
                    },
                )
            }
        ),
        ExampleAction(
            title = "Compact card",
            content = {
                CompactCard(
                    onClick = { },
                    modifier = Modifier
                        .width(180.dp),
                    image = {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                            painter = painterResource(id = R.drawable.card_thumbnail),
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(
                            text = "Title goes here",
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        )
                    },
                    subtitle = {
                        Text(
                            text = "Secondary · text",
                            modifier = Modifier.padding(
                                top = 4.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                        )
                    },
                )
            }
        ),
        ExampleAction(
            title = "Wide standard card",
            content = {
                WideCardContainer(
                    modifier = Modifier,
                    imageCard = {
                        Card(
                            onClick = { },
                            interactionSource = it,
                            colors = CardDefaults.colors(containerColor = Color.Transparent)
                        ) {
                            Image(
                                modifier = Modifier
                                    .width(180.dp)
                                    .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                                painter = painterResource(id = R.drawable.card_thumbnail),
                                contentDescription = null,
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Title goes here",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )
                    },
                    subtitle = {
                        Text(
                            text = "Secondary · text",
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    },
                )
            }
        ),
        ExampleAction(
            title = "Wide classic card",
            content = {
                WideClassicCard(
                    onClick = { },
                    modifier = Modifier,
                    image = {
                        Image(
                            modifier = Modifier
                                .width(180.dp)
                                .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                            painter = painterResource(id = R.drawable.card_thumbnail),
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(
                            text = "Title goes here",
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        )
                    },
                    subtitle = {
                        Text(
                            text = "Secondary · text",
                            modifier = Modifier.padding(
                                top = 4.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                        )
                    },
                    description = {
                        Text(
                            text = "A cruel tryst of fate deserts a loyal dog from his master infin...",
                            modifier = Modifier
                                .width(200.dp)
                                .padding(
                                    top = 4.dp,
                                    start = 8.dp,
                                    end = 8.dp,
                                    bottom = 8.dp
                                )
                        )
                    }
                )
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}
