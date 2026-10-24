package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.data.preferences.AppPreferencesRepository
import com.example.ui.AppShell
import com.example.ui.theme.AgriCareTheme
import com.example.ui.theme.WarmOffWhite
import com.example.util.AgriCareLocalizedApp
import com.example.util.AppLanguage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepo = AppPreferencesRepository(applicationContext)

        setContent {
            val userPrefs by preferencesRepo.userPreferencesFlow.collectAsState(
                initial = com.example.data.preferences.UserPreferences()
            )
            val coroutineScope = rememberCoroutineScope()

            val isDark = when (userPrefs.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            AgriCareLocalizedApp(language = userPrefs.language) {
                AgriCareTheme(darkTheme = isDark) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = WarmOffWhite
                    ) {
                        AppShell(
                            preferencesRepo = preferencesRepo,
                            currentLanguage = userPrefs.language,
                            onboardingCompleted = userPrefs.onboardingCompleted,
                            isLoaded = userPrefs.isLoaded,
                            onLanguageChange = { newLang ->
                                coroutineScope.launch {
                                    preferencesRepo.setLanguage(newLang)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
