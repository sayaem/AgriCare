package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.preferences.AppPreferencesRepository
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.theme.AlertError
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import com.example.util.AppLanguage
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    preferencesRepo: AppPreferencesRepository,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onResetOnboarding: () -> Unit,
    onClearAllData: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val userPrefs by preferencesRepo.userPreferencesFlow.collectAsState(
        initial = com.example.data.preferences.UserPreferences()
    )

    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("profile_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_profile),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card matching Mockup Screen 13
            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SageSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "S",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.default_user_name),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.user_role_subtitle),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            // Language Switcher Card (PRD 10: Language selection must persist and apply immediately)
            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.language_selection),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (currentLanguage == AppLanguage.ENGLISH) ForestGreenPrimary else PureWhite)
                                    .clickable { onLanguageChange(AppLanguage.ENGLISH) }
                                    .padding(horizontal = 12.dp)
                                    .testTag("profile_lang_en"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "English",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (currentLanguage == AppLanguage.ENGLISH) PureWhite else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (currentLanguage == AppLanguage.BANGLA) ForestGreenPrimary else PureWhite)
                                    .clickable { onLanguageChange(AppLanguage.BANGLA) }
                                    .padding(horizontal = 12.dp)
                                    .testTag("profile_lang_bn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "বাংলা",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (currentLanguage == AppLanguage.BANGLA) PureWhite else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Notification Preference Toggle
            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_notifications),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Task reminders & seasonal alerts",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                                )
                            }
                        }
                        Switch(
                            checked = userPrefs.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch {
                                    preferencesRepo.setNotificationsEnabled(enabled)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = ForestGreenPrimary
                            ),
                            modifier = Modifier.testTag("notification_pref_switch")
                        )
                    }
                }
            }

            // App Information & Legal Links (PRD 6.18: Provide actual readable pages. Do not link to blank routes)
            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    ProfileMenuRow(
                        icon = Icons.Default.Eco,
                        title = stringResource(R.string.title_about),
                        testTag = "menu_about",
                        onClick = onNavigateToAbout
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileMenuRow(
                        icon = Icons.Default.Security,
                        title = stringResource(R.string.title_privacy),
                        testTag = "menu_privacy",
                        onClick = onNavigateToPrivacy
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileMenuRow(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.title_terms),
                        testTag = "menu_terms",
                        onClick = onNavigateToTerms
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileMenuRow(
                        icon = Icons.Default.RestartAlt,
                        title = "Reset Onboarding",
                        testTag = "menu_reset_onboarding",
                        onClick = onResetOnboarding
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileMenuRow(
                        icon = Icons.Default.DeleteForever,
                        title = stringResource(R.string.settings_data_clear),
                        iconTint = AlertError,
                        titleColor = AlertError,
                        testTag = "menu_clear_data",
                        onClick = { showClearDataDialog = true }
                    )
                }
            }
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.settings_data_clear_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_data_clear_msg),
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AlertError),
                    modifier = Modifier.testTag("confirm_clear_all_data_btn")
                ) {
                    Text(stringResource(R.string.action_confirm), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("about_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_about),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "AgriCare",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                    )
                    Text(
                        text = "Zero-Budget Plant & Crop Health Assistant",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AgriCare is an Android-first crop companion built for Bangladeshi farmers, smallholders, and gardeners. Our mission is to help growers understand plant diseases earlier with practical, safe, and actionable steps.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, color = TextPrimary)
                    )
                }
            }

            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Zero-Budget Principle",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Built entirely without paid services or required subscription fees. Starter guides and saved records operate locally offline on your device, while Gemini Vision analysis runs via free-tier access.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextSecondary)
                    )
                }
            }

            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Attribution & Partners",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Agricultural guidance is compiled from public recommendations by the Bangladesh Rice Research Institute (BRRI), Bangladesh Agricultural Research Institute (BARI), and Department of Agricultural Extension (DAE).",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextSecondary)
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("privacy_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_privacy),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your Privacy at AgriCare",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We believe agricultural tools should respect farmers' privacy. You do not need to create an account, provide an email, or share phone numbers to use AgriCare.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextPrimary)
                    )
                }
            }

            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Image Processing & Gemini AI",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "When you choose to analyze an affected leaf or plant, only the compressed photo and optional crop name are transmitted to Google Gemini Vision for diagnostic suggestions. We do not transmit EXIF metadata, GPS coordinates, or unselected personal photos.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextSecondary)
                    )
                }
            }

            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Local Storage & Data Control",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All your crop entries, reminders, and diagnosis histories are stored strictly locally on your Android device in a secure SQLite database. You can delete individual records or wipe all data at any time via Settings.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextSecondary)
                    )
                }
            }
        }
    }
}

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("terms_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_terms),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Informational Guidance Notice",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AgriCare provides AI-assisted visual suggestions based on leaf symptoms. It is an educational and supportive tool, not a certified agronomic or laboratory diagnostic service.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextPrimary)
                    )
                }
            }

            item {
                AgriCareCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Expert Escalation & Chemical Safety",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "For severe, widespread, or high-risk crop infections, always consult your local Upazila Agriculture Officer (UAO) or Sub-Assistant Agriculture Officer (SAAO). Never apply chemical treatments without reading and strictly adhering to official product labels and wearing required protective equipment.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp, color = TextSecondary)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    testTag: String,
    iconTint: Color = ForestGreenPrimary,
    titleColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = titleColor
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
