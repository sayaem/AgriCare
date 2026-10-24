package com.example.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BANGLA("bn", "Bangla", "বাংলা")
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

object LocaleHelper {
    fun createLocalizedContext(context: Context, language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.BANGLA -> Locale("bn", "BD")
            AppLanguage.ENGLISH -> Locale("en", "US")
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}

/**
 * Wraps content with localized context so all stringResource() calls
 * immediately reflect the selected AppLanguage without restarting the app.
 */
@Composable
fun AgriCareLocalizedApp(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val currentContext = LocalContext.current
    val localizedContext = LocaleHelper.createLocalizedContext(currentContext, language)
    val localizedConfiguration = localizedContext.resources.configuration

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration,
        LocalAppLanguage provides language
    ) {
        content()
    }
}
