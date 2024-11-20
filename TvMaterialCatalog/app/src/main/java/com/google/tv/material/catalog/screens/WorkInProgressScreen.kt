package com.google.tv.material.catalog.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text

@Composable
fun WorkInProgressScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "WORK IN PROGRESS",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )
    }
}