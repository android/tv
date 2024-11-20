package com.google.tv.material.catalog.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.tv.material3.AssistChip
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.Icon
import androidx.tv.material3.InputChip
import androidx.tv.material3.SuggestionChip
import androidx.tv.material3.Text
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChipsScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Assist Chip",
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AssistChip(onClick = { }) {
                        Text(text = "Label")
                    }

                    AssistChip(
                        onClick = { },
                        leadingIcon = editIcon
                    ) {
                        Text(text = "Label")
                    }
                }
            }
        ),
        ExampleAction(
            title = "Filter Chip",
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var isSelected1 by remember { mutableStateOf(false) }
                    FilterChip(
                        selected = isSelected1,
                        onClick = { isSelected1 = !isSelected1 }
                    ) {
                        Text(text = "Label")
                    }

                    var isSelected2 by remember { mutableStateOf(false) }
                    FilterChip(
                        selected = isSelected2,
                        onClick = { isSelected2 = !isSelected2 },
                        leadingIcon = checkIcon
                    ) {
                        Text(text = "Label")
                    }
                }
            }
        ),
        ExampleAction(
            title = "Input Chip",
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var isSelected1 by remember { mutableStateOf(false) }
                    InputChip(
                        selected = isSelected1,
                        onClick = { isSelected1 = !isSelected1 }
                    ) {
                        Text(text = "Label")
                    }

                    var isSelected2 by remember { mutableStateOf(false) }
                    InputChip(
                        selected = isSelected2,
                        onClick = { isSelected2 = !isSelected2 },
                        leadingIcon = editIcon,
                        trailingIcon = clearIcon
                    ) {
                        Text(text = "Label")
                    }
                }
            }
        ),
        ExampleAction(
            title = "Suggestion Chip",
            content = {
                SuggestionChip(onClick = { }) {
                    Text(text = "Label")
                }
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}

private val editIcon = @Composable {
    Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = "Edit icon",
    )
}

private val checkIcon = @Composable {
    Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Check icon",
    )
}

private val clearIcon = @Composable {
    Icon(
        imageVector = Icons.Default.Clear,
        contentDescription = "Clear icon",
    )
}
