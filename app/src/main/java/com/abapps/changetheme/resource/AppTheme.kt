package com.abapps.changetheme.resource

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.abapps.changetheme.resource.strings.IStringResources
import com.abapps.changetheme.ui.theme.ChangeThemeTheme
import com.abapps.changetheme.util.LanguageCode
import com.abapps.changetheme.util.LocalizationManager

private val localStringResources = staticCompositionLocalOf<IStringResources> {
    error("CompositionLocal IStringResources not present")
}

internal val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@Composable
internal fun AppTheme(
    languageCode: LanguageCode,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemIsDark) }
    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState,
        localStringResources provides LocalizationManager.getStringResources(languageCode),
        LocalLayoutDirection provides LocalizationManager.getLayoutDirection(languageCode),
    ) {
        val isDark by isDarkState
        SystemAppearance(!isDark)
        ChangeThemeTheme(darkTheme = isDark, content = content)
        // Or use DesignSystemTheme(useDarkTheme = isDark, content = content) if you have it instead of ChangeThemeTheme
    }
}

@Composable
private fun SystemAppearance(isDark: Boolean) {
    val view = LocalView.current
    LaunchedEffect(isDark) {
        val window = (view.context as Activity).window
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isDark
            isAppearanceLightNavigationBars = isDark
        }
    }
}

object Resources {
    val strings: IStringResources
        @Composable
        @ReadOnlyComposable
        get() = localStringResources.current
}