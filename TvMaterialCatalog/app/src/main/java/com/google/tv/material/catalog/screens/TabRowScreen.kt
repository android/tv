package com.google.tv.material.catalog.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground
import com.google.tv.material.catalog.R

private val tabs = listOf("Search", "Home", "Movies", "Shows", "Library", "Settings")

private fun getTabIndex(text: String): Int {
    return tabs.indexOf(text)
}

@Composable
fun TabRowScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Pill Indicator",
            content = {
                PillIndicatorTabRow()
            }
        ),
        ExampleAction(
            title = "Underlined Indicator",
            content = {
                UnderlinedIndicatorTabRow()
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}

@Composable
private fun PillIndicatorTabRow() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTabIndex,
                    onFocus = { selectedTabIndex = index },
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        TabPanels(selectedTabIndex = selectedTabIndex)
    }
}

@Composable
private fun UnderlinedIndicatorTabRow() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            separator = { Spacer(modifier = Modifier.width(16.dp)) },
            indicator = { tabPositions, doesTabRowHaveFocus ->
                TabRowDefaults.UnderlinedIndicator(
                    currentTabPosition = tabPositions[selectedTabIndex],
                    doesTabRowHaveFocus = doesTabRowHaveFocus
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTabIndex,
                    onFocus = { selectedTabIndex = index },
                    colors = TabDefaults.underlinedIndicatorTabColors(),
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        TabPanels(selectedTabIndex = selectedTabIndex)
    }
}

@Composable
private fun TabPanels(selectedTabIndex: Int) {
    AnimatedContent(targetState = selectedTabIndex, label = "") {
        when (it) {
            getTabIndex("Search") -> {
                Column(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_search),
                        contentDescription = null
                    )

                    Image(
                        painter = painterResource(id = R.drawable.img_keyboard),
                        contentDescription = null
                    )
                }
            }
            getTabIndex("Home") -> {
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_fc),
                        contentDescription = null
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(3) {
                            Image(
                                modifier = Modifier
                                    .width(175.dp)
                                    .aspectRatio(16f / 9),
                                painter = painterResource(id = R.drawable.img_card),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            getTabIndex("Movies"), getTabIndex("Shows") -> {
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_block_1),
                        contentDescription = null
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(3) {
                            Image(
                                modifier = Modifier
                                    .width(175.dp)
                                    .aspectRatio(16f / 9),
                                painter = painterResource(id = R.drawable.img_card),
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .height(24.dp)
                            .fillMaxWidth()
                    )

                    Image(
                        painter = painterResource(id = R.drawable.img_block_2),
                        contentDescription = null
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(3) {
                            Image(
                                modifier = Modifier
                                    .width(175.dp)
                                    .aspectRatio(16f / 9),
                                painter = painterResource(id = R.drawable.img_card),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            getTabIndex("Library") -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Column(modifier = Modifier.padding(top = 86.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_block_3),
                            contentDescription = null
                        )
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        )
                        Image(
                            painter = painterResource(id = R.drawable.img_block_1),
                            contentDescription = null
                        )
                    }
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.img_block_2),
                            contentDescription = null
                        )
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(3) {
                                Image(
                                    modifier = Modifier
                                        .width(175.dp)
                                        .aspectRatio(16f / 9),
                                    painter = painterResource(id = R.drawable.img_card),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
            getTabIndex("Settings") -> {
                Row(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(5) {
                            Image(
                                modifier = Modifier.height(25.dp),
                                painter = painterResource(id = R.drawable.img_block_4),
                                contentDescription = null
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Image(
                                    modifier = Modifier.size(144.dp, 72.dp),
                                    painter = painterResource(id = R.drawable.img_card),
                                    contentDescription = null
                                )
                                Image(
                                    modifier = Modifier.size(144.dp, 72.dp),
                                    painter = painterResource(id = R.drawable.img_card),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
