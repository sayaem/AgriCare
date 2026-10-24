package com.example.ui.navigation

enum class AgriCareDestination(val route: String) {
    SPLASH("splash"),
    ONBOARDING("onboarding"),
    HOME("home"),
    MY_CROPS("my_crops"),
    DIAGNOSE("diagnose"),
    HISTORY("history"),
    CALENDAR("calendar"),
    PROFILE("profile"),
    CROP_LIBRARY("crop_library"),
    NOTIFICATIONS("notifications"),
    ANALYSIS_RESULT("analysis_result"),
    ANALYSIS_LOADING("analysis_loading"),
    TREATMENT_PLAN("treatment_plan"),
    ABOUT("about"),
    PRIVACY("privacy"),
    TERMS("terms")
}

data class NavigationState(
    val currentDestination: AgriCareDestination = AgriCareDestination.HOME,
    val backStack: List<AgriCareDestination> = listOf(AgriCareDestination.HOME)
)
