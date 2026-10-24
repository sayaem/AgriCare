package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AgriCareLightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = TextOnDark,
    primaryContainer = SageSurface,
    onPrimaryContainer = ForestGreenDeep,
    secondary = LeafGreenAccent,
    onSecondary = TextOnDark,
    secondaryContainer = MintLight,
    onSecondaryContainer = ForestGreenDeep,
    background = WarmOffWhite,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SageSurface,
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
    error = AlertError,
    errorContainer = AlertErrorContainer,
    onError = TextOnDark,
    onErrorContainer = AlertError
)

private val AgriCareDarkColorScheme = darkColorScheme(
    primary = LeafGreenAccent,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkTextPrimary,
    secondary = MintLight,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceVariant,
    error = AlertError,
    onError = TextOnDark
)

@Composable
fun AgriCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // PRD Section 6.17: Default theme should be light to match the reference.
    val colorScheme = if (darkTheme) AgriCareDarkColorScheme else AgriCareLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AgriCareTypography,
        shapes = AgriCareShapes,
        content = content
    )
}
