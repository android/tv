package com.google.tv.material.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.LayoutDirection
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import com.google.tv.material.catalog.colorutils.Scheme.Companion.dark
import com.google.tv.material.catalog.colorutils.Scheme.Companion.light
import com.google.tv.material.catalog.colorutils.toColorScheme

@Composable
fun App() {
    var themeMode by remember { mutableStateOf(Mode.Dark) }
    var seedColor by remember { mutableStateOf(PurpleSeedColor) }
    var layoutDirection by remember { mutableStateOf(LayoutDirection.Ltr) }
    var fontScale by remember { mutableFloatStateOf(1.0f) }

    var isThemeSelectorExpanded by remember { mutableStateOf(false) }
    var isFontScaleSelectorExpanded by remember { mutableStateOf(false) }

    val argbColor = seedColor.color.toArgb()
    val colorScheme = if (themeMode == Mode.Dark) dark(argbColor) else light(argbColor)

    AppProviders(
        seedColor = seedColor,
        themeMode = themeMode,
        layoutDirection = layoutDirection,
        fontScale = fontScale,
    ) {
        MaterialTheme(colorScheme = colorScheme.toColorScheme()) {
            val themeFocus = remember { FocusRequester() }
            val fontFocus = remember { FocusRequester() }
            ThemeAndColorModeSelector(
                isExpanded = isThemeSelectorExpanded,
                onClose = {
                    isThemeSelectorExpanded = false
                    themeFocus.requestFocus()
                },
                onSeedColorChange = { seedColor = it },
                onThemeModeChange = { themeMode = it },
            ) {
                FontScaleAndLayoutDirectionSelector(
                    isExpanded = isFontScaleSelectorExpanded,
                    onClose = {
                        isFontScaleSelectorExpanded = false
                        fontFocus.requestFocus()
                    },
                    onLayoutDirectionChange = { layoutDirection = it },
                    onFontScaleChange = { fontScale = it }
                ) {
                    Surface(
                        Modifier.fillMaxSize(),
                        shape = RectangleShape
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            NavigationGraph(
                                themeFocus, fontFocus,
                                onThemeColorModeClick = {
                                    isThemeSelectorExpanded = true
                                },
                                onFontScaleClick = {
                                    isFontScaleSelectorExpanded = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
