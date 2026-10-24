package com.example.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ai.AgriDiagnosisResult
import com.example.data.local.AgriCareDatabase
import com.example.data.local.DiagnosisEntity
import com.example.data.preferences.AppPreferencesRepository
import com.example.ui.components.AgriCareBottomBar
import com.example.ui.components.AgriCareEmptyState
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.navigation.AgriCareDestination
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AnalysisLoadingScreen
import com.example.ui.screens.AnalysisResultScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.CropLibraryScreen
import com.example.ui.screens.DiagnoseEntryScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyCropsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TermsOfServiceScreen
import com.example.ui.screens.TreatmentPlanScreen
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.WarmOffWhite
import com.example.util.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppShell(
    preferencesRepo: AppPreferencesRepository,
    currentLanguage: AppLanguage,
    onboardingCompleted: Boolean,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { AgriCareDatabase.getInstance(context) }

    var currentDestination by remember { mutableStateOf(AgriCareDestination.SPLASH) }
    var backStack by remember { mutableStateOf<List<AgriCareDestination>>(emptyList()) }

    // Diagnosis Workflow State
    var activeImageUri by remember { mutableStateOf<Uri?>(null) }
    var activeUserNotes by remember { mutableStateOf<String?>(null) }
    var activeDiagnosisResult by remember { mutableStateOf<AgriDiagnosisResult?>(null) }

    fun navigateTo(dest: AgriCareDestination, clearStack: Boolean = false) {
        if (dest != currentDestination) {
            val newStack = if (clearStack || dest == AgriCareDestination.HOME) {
                listOf(AgriCareDestination.HOME)
            } else {
                backStack + dest
            }
            backStack = newStack
            currentDestination = dest
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            val newStack = backStack.dropLast(1)
            backStack = newStack
            currentDestination = newStack.last()
            return true
        } else if (currentDestination != AgriCareDestination.HOME) {
            currentDestination = AgriCareDestination.HOME
            backStack = listOf(AgriCareDestination.HOME)
            return true
        }
        return false
    }

    BackHandler(enabled = currentDestination != AgriCareDestination.HOME && currentDestination != AgriCareDestination.SPLASH) {
        navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("agricare_app_shell"),
        containerColor = WarmOffWhite,
        bottomBar = {
            // Show bottom bar on primary dashboard destinations
            val showBottomBar = currentDestination in listOf(
                AgriCareDestination.HOME,
                AgriCareDestination.MY_CROPS,
                AgriCareDestination.HISTORY,
                AgriCareDestination.CALENDAR,
                AgriCareDestination.PROFILE
            )
            if (showBottomBar) {
                AgriCareBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { dest -> navigateTo(dest) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = {
                    fadeIn(tween(200)).togetherWith(fadeOut(tween(200)))
                },
                label = "app_destination_transition"
            ) { destination ->
                when (destination) {
                    AgriCareDestination.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = {
                                if (onboardingCompleted) {
                                    navigateTo(AgriCareDestination.HOME, clearStack = true)
                                } else {
                                    navigateTo(AgriCareDestination.ONBOARDING, clearStack = true)
                                }
                            }
                        )
                    }

                    AgriCareDestination.ONBOARDING -> {
                        OnboardingScreen(
                            currentLanguage = currentLanguage,
                            onLanguageChange = onLanguageChange,
                            onCompleteOnboarding = {
                                coroutineScope.launch {
                                    preferencesRepo.setOnboardingCompleted(true)
                                }
                                navigateTo(AgriCareDestination.HOME, clearStack = true)
                            }
                        )
                    }

                    AgriCareDestination.HOME -> {
                        HomeScreen(
                            diagnosisDao = db.diagnosisDao(),
                            onNavigateToDiagnose = { _ -> navigateTo(AgriCareDestination.DIAGNOSE) },
                            onNavigateToMyCrops = { navigateTo(AgriCareDestination.MY_CROPS) },
                            onNavigateToCalendar = { navigateTo(AgriCareDestination.CALENDAR) },
                            onNavigateToHistory = { navigateTo(AgriCareDestination.HISTORY) },
                            onNavigateToProfile = { navigateTo(AgriCareDestination.PROFILE) },
                            onNavigateToNotifications = { navigateTo(AgriCareDestination.NOTIFICATIONS) },
                            onNavigateToCropLibrary = { navigateTo(AgriCareDestination.CROP_LIBRARY) }
                        )
                    }

                    AgriCareDestination.MY_CROPS -> {
                        MyCropsScreen(
                            plantDao = db.plantDao(),
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.CROP_LIBRARY -> {
                        CropLibraryScreen(
                            guideArticleDao = db.guideArticleDao(),
                            database = db,
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.HISTORY -> {
                        HistoryScreen(
                            diagnosisDao = db.diagnosisDao(),
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.PROFILE -> {
                        ProfileScreen(
                            preferencesRepo = preferencesRepo,
                            currentLanguage = currentLanguage,
                            onLanguageChange = onLanguageChange,
                            onNavigateToAbout = { navigateTo(AgriCareDestination.ABOUT) },
                            onNavigateToPrivacy = { navigateTo(AgriCareDestination.PRIVACY) },
                            onNavigateToTerms = { navigateTo(AgriCareDestination.TERMS) },
                            onResetOnboarding = {
                                coroutineScope.launch {
                                    preferencesRepo.setOnboardingCompleted(false)
                                    navigateTo(AgriCareDestination.ONBOARDING, clearStack = true)
                                }
                            },
                            onClearAllData = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.clearAllTables()
                                    }
                                    preferencesRepo.clearAllPreferences()
                                    navigateTo(AgriCareDestination.ONBOARDING, clearStack = true)
                                }
                            },
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.ABOUT -> {
                        AboutScreen(onBack = { navigateBack() })
                    }

                    AgriCareDestination.PRIVACY -> {
                        PrivacyPolicyScreen(onBack = { navigateBack() })
                    }

                    AgriCareDestination.TERMS -> {
                        TermsOfServiceScreen(onBack = { navigateBack() })
                    }

                    AgriCareDestination.DIAGNOSE -> {
                        DiagnoseEntryScreen(
                            onImageSelected = { uri, notes ->
                                activeImageUri = uri
                                activeUserNotes = notes
                                navigateTo(AgriCareDestination.ANALYSIS_LOADING)
                            },
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.ANALYSIS_LOADING -> {
                        AnalysisLoadingScreen(
                            imageUri = activeImageUri ?: Uri.EMPTY,
                            userNotes = activeUserNotes,
                            currentLanguage = currentLanguage,
                            onAnalysisSuccess = { result, uri ->
                                activeDiagnosisResult = result
                                activeImageUri = uri
                                navigateTo(AgriCareDestination.ANALYSIS_RESULT)
                            },
                            onAnalysisError = { _ ->
                                navigateBack()
                            },
                            onCancel = { navigateBack() }
                        )
                    }

                    AgriCareDestination.ANALYSIS_RESULT -> {
                        AnalysisResultScreen(
                            diagnosisResult = activeDiagnosisResult ?: AgriDiagnosisResult(
                                issueTitle = "Plant Inspection Result",
                                assessmentLevel = "uncertain"
                            ),
                            imageUri = activeImageUri ?: Uri.EMPTY,
                            onViewTreatmentPlan = { navigateTo(AgriCareDestination.TREATMENT_PLAN) },
                            onSaveToHistory = {
                                coroutineScope.launch {
                                    activeDiagnosisResult?.let { res ->
                                        db.diagnosisDao().insertDiagnosis(
                                            DiagnosisEntity(
                                                cropGuess = res.cropGuess,
                                                issueTitle = res.issueTitle,
                                                assessmentLevel = res.assessmentLevel,
                                                symptoms = res.visibleSymptoms,
                                                alternativeCauses = res.alternativeCauses,
                                                immediateSteps = res.immediateSteps.map { it.title },
                                                preventionSteps = res.preventionSteps,
                                                escalationAdvice = res.escalationAdvice,
                                                language = res.language
                                            )
                                        )
                                    }
                                }
                            },
                            onAddCrop = { navigateTo(AgriCareDestination.MY_CROPS) },
                            onAnalyzeAnother = { navigateTo(AgriCareDestination.DIAGNOSE) },
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.TREATMENT_PLAN -> {
                        TreatmentPlanScreen(
                            steps = activeDiagnosisResult?.immediateSteps ?: emptyList(),
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.CALENDAR -> {
                        CalendarScreen(
                            taskDao = db.cropTaskDao(),
                            onBack = { navigateBack() }
                        )
                    }

                    AgriCareDestination.NOTIFICATIONS -> {
                        NotificationsScreen(
                            taskDao = db.cropTaskDao(),
                            onBack = { navigateBack() }
                        )
                    }

                    else -> {
                        HomeScreen(
                            diagnosisDao = db.diagnosisDao(),
                            onNavigateToDiagnose = { _ -> navigateTo(AgriCareDestination.DIAGNOSE) },
                            onNavigateToMyCrops = { navigateTo(AgriCareDestination.MY_CROPS) },
                            onNavigateToCalendar = { navigateTo(AgriCareDestination.CALENDAR) },
                            onNavigateToHistory = { navigateTo(AgriCareDestination.HISTORY) },
                            onNavigateToProfile = { navigateTo(AgriCareDestination.PROFILE) },
                            onNavigateToNotifications = { navigateTo(AgriCareDestination.NOTIFICATIONS) },
                            onNavigateToCropLibrary = { navigateTo(AgriCareDestination.CROP_LIBRARY) }
                        )
                    }
                }
            }
        }
    }
}

