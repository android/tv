package com.google.tv.material.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.tv.material.catalog.screens.ButtonsScreen
import com.google.tv.material.catalog.screens.CardsScreen
import com.google.tv.material.catalog.screens.ChipsScreen
import com.google.tv.material.catalog.screens.ColorsScreen
import com.google.tv.material.catalog.screens.FeaturedCarouselScreen
import com.google.tv.material.catalog.screens.ImmersiveListScreen
import com.google.tv.material.catalog.screens.InteractionsScreen
import com.google.tv.material.catalog.screens.ListsScreen
import com.google.tv.material.catalog.screens.MotionScreen
import com.google.tv.material.catalog.screens.TabRowScreen
import com.google.tv.material.catalog.screens.TypographyScreen
import com.google.tv.material.catalog.screens.WorkInProgressScreen

@Composable
fun NavigationGraph(
    themeFocus: FocusRequester,
    fontFocus: FocusRequester,
    onThemeColorModeClick: () -> Unit,
    onFontScaleClick: () -> Unit
) {
    val navHostController = LocalNavController.current
    NavHost(navController = navHostController, startDestination = NavGraph.Home.routeName) {
        destinations.forEach { destination ->
            composable(destination.routeName) {
                destination.composable {
                    AppBar(
                        themeFocus, fontFocus,
                        onThemeColorModeClick = onThemeColorModeClick,
                        onFontScaleClick = onFontScaleClick
                    )
                }
            }
        }
    }
}

enum class NavGraph(
    val routeName: String,
    val composable: @Composable (appBar: @Composable () -> Unit) -> Unit
) {
    Home(
        routeName = "home",
        composable = { appBar ->
            Column {
                appBar()
                HomeGrid()
            }
        }
    ),

    // foundations
    Color(
        routeName = "color",
        composable = { appBar ->
            Column {
                appBar()
                ColorsScreen()
            }
        }
    ),
    Typography(
        routeName = "typography",
        composable = { appBar ->
            Column {
                appBar()
                TypographyScreen()
            }
        }
    ),
    Motion(
        routeName = "motion",
        composable = { appBar ->
            Column {
                appBar()
                MotionScreen()
            }
        }
    ),
    Interaction(
        routeName = "interaction",
        composable = { appBar ->
            Column {
                appBar()
                InteractionsScreen()
            }
        }
    ),

    // components
    Buttons(
        routeName = "buttons",
        composable = { appBar ->
            Column {
                appBar()
                ButtonsScreen()
            }
        }
    ),
    Cards(
        routeName = "cards",
        composable = { appBar ->
            Column {
                appBar()
                CardsScreen()
            }
        }
    ),
    Chips(
        routeName = "chips",
        composable = { appBar ->
            Column {
                appBar()
                ChipsScreen()
            }
        }
    ),
    Lists(
        routeName = "lists",
        composable = { appBar ->
            Column {
                appBar()
                ListsScreen()
            }
        }
    ),
    ImmersiveList(
        routeName = "immersive-list",
        composable = { appBar ->
            Box {
                ImmersiveListScreen()
                appBar()
            }
        }
    ),
    FeaturedCarousel(
        routeName = "featured-carousel",
        composable = { appBar ->
            Box {
                FeaturedCarouselScreen()
                appBar()
            }
        }
    ),
    TabRow(
        routeName = "tab-row",
        composable = { appBar ->
            Column {
                appBar()
                TabRowScreen()
            }
        }
    ),
    NavigationDrawer(
        routeName = "nav-drawer",
        composable = {
            WorkInProgressScreen()
        }
    ),
    ModalDialog(
        routeName = "modal-dialog",
        composable = {
            WorkInProgressScreen()
        }
    ),
    TextFields(
        routeName = "text-fields",
        composable = {
            WorkInProgressScreen()
        }
    ),
    VideoPlayer(
        routeName = "video-player",
        composable = {
            WorkInProgressScreen()
        }
    ),
}

val destinations = listOf(
    NavGraph.Home,

    // foundation
    NavGraph.Color,
    NavGraph.Typography,
    NavGraph.Motion,
    NavGraph.Interaction,

    // components
    NavGraph.Buttons,
    NavGraph.Cards,
    NavGraph.Chips,
    NavGraph.Lists,
    NavGraph.ImmersiveList,
    NavGraph.FeaturedCarousel,
    NavGraph.NavigationDrawer,
    NavGraph.TabRow,
    NavGraph.ModalDialog,
    NavGraph.TextFields,
    NavGraph.VideoPlayer,
)

val LocalNavController = compositionLocalOf<NavHostController> {
    throw Error("This should not be reached")
}
