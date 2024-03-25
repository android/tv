package com.google.jetfit.presentation.screens.training.challenge

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.google.jetfit.presentation.screens.training.composable.TrainingImageWithGradient
import com.google.jetfit.presentation.screens.training.composable.showFullscreenIfChildrenAreFocused
import com.google.jetfit.presentation.theme.JetFitTheme

@Composable
fun ChallengeScreen(challengeViewModel: ChallengeViewModel = hiltViewModel()) {
    val state by challengeViewModel.state.collectAsState()
    ChallengeScreenContent(state)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalTvMaterial3Api::class)
@Composable
private fun ChallengeScreenContent(
    state: ChallengeUiState,
) {
    val pagerState = rememberPagerState { state.challengePages.size }
    val alpha: Float by animateFloatAsState(
        if (pagerState.currentPage == 0) 1f else 0.2f,
        label = "alpha effect"
    )
    Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background).fillMaxSize()) {
        TrainingImageWithGradient(
            Modifier.align(Alignment.TopEnd),
            state.imageUrl,
            "Challenge Image",
            imagePassThrough = alpha
        )
        VerticalPager(state = pagerState, beyondBoundsPageCount = 2) { pageIndex ->
            Box(modifier = Modifier.fillMaxSize().showFullscreenIfChildrenAreFocused()) {
                when (state.challengePages[pageIndex]) {
                    ChallengePages.ChallengeDetails -> {
                        ChallengeDetails(state)
                    }

                    ChallengePages.ChallengeTabs -> {
                        ChallengeTabs(state)
                    }
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_TELEVISION,
    device = "id:tv_4k"
)
@Composable
fun ChallengeScreenPreview() {
    JetFitTheme {
        ChallengeScreen()
    }
}