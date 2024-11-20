package com.google.tv.material.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text

@Composable
fun ComponentsGridCard(
    component: Component,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val image = getHomeGridCardImage(imageArg = component.imageArg)

    StandardCardContainer(
        modifier = modifier,
        imageCard = {
            Card(
                onClick = {
                    onClick()
                },
                interactionSource = it,
                colors = CardDefaults.colors(containerColor = Color.Transparent)
            ) {
                Image(painter = painterResource(id = image), contentDescription = null)
            }
        },
        title = {
            Text(
                text = component.title,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    )
}
