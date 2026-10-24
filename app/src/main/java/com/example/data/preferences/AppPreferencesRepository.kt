package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.emptyPreferences
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "agricare_preferences")

data class UserPreferences(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val themeMode: String = "light", // light, dark, system
    val onboardingCompleted: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val aiConsentAccepted: Boolean = false,
    val retainImagesLocally: Boolean = true,
    val isLoaded: Boolean = false
)

class AppPreferencesRepository(private val context: Context) {
    private object PreferenceKeys {
        val LANGUAGE = stringPreferencesKey("app_language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val AI_CONSENT_ACCEPTED = booleanPreferencesKey("ai_consent_accepted")
        val RETAIN_IMAGES = booleanPreferencesKey("retain_images")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val langStr = preferences[PreferenceKeys.LANGUAGE] ?: "en"
            val language = if (langStr == "bn") AppLanguage.BANGLA else AppLanguage.ENGLISH
            val themeMode = preferences[PreferenceKeys.THEME_MODE] ?: "light"
            val onboarding = preferences[PreferenceKeys.ONBOARDING_COMPLETED] ?: false
            val notifications = preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: true
            val consent = preferences[PreferenceKeys.AI_CONSENT_ACCEPTED] ?: false
            val retainImages = preferences[PreferenceKeys.RETAIN_IMAGES] ?: true

            UserPreferences(
                language = language,
                themeMode = themeMode,
                onboardingCompleted = onboarding,
                notificationsEnabled = notifications,
                aiConsentAccepted = consent,
                retainImagesLocally = retainImages,
                isLoaded = true
            )
        }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language.code
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAiConsentAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AI_CONSENT_ACCEPTED] = accepted
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { it.clear() }
    }
}
