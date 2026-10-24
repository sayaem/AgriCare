package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.DiagnosisDao
import com.example.data.local.DiagnosisEntity
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareHomeHeader
import com.example.ui.components.AgriCarePill
import com.example.ui.components.LeafTipThumbnail
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LeafGreenAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import java.util.Calendar

data class CuratedTip(
    val titleEn: String,
    val titleBn: String,
    val source: String
)

private val CURATED_TIPS = listOf(
    CuratedTip(
        titleEn = "Check for early signs of leaf spot in rice plants, especially after rain.",
        titleBn = "বৃষ্টির পর ধানের পাতায় বাদামি দাগ রোগের প্রাথমিক লক্ষণ খেয়াল করুন।",
        source = "BRRI Field Guide"
    ),
    CuratedTip(
        titleEn = "Inspect tomato leaf undersides in high humidity for early powdery mildew.",
        titleBn = "অতিরিক্ত আর্দ্রতায় টমেটো পাতার নিচের অংশে গুঁড়ো ছত্রাকের উপস্থিতি পরীক্ষা করুন।",
        source = "BARI Horticulture Advisory"
    ),
    CuratedTip(
        titleEn = "Ensure potato ridges have good drainage to prevent tuber soft rot.",
        titleBn = "আলুর নরম পচন রোগ রোধ করতে আইলে উপযুক্ত নিষ্কাশন ব্যবস্থা নিশ্চিত করুন।",
        source = "DAE Crop Advisory"
    )
)

@Composable
fun HomeScreen(
    diagnosisDao: DiagnosisDao?,
    onNavigateToDiagnose: (startWithCamera: Boolean) -> Unit,
    onNavigateToMyCrops: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCropLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Real time-aware greeting
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 0..11 -> stringResource(R.string.greeting_morning)
        in 12..16 -> stringResource(R.string.greeting_afternoon)
        else -> stringResource(R.string.greeting_evening)
    }

    // Collect real diagnoses from Room database
    val diagnosesFlow = remember(diagnosisDao) { diagnosisDao?.getAllDiagnoses() }
    val diagnosisList by diagnosesFlow?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }

    var tipIndex by remember { mutableIntStateOf(0) }
    var isTipVisible by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("home_screen_container")
    ) {
        // Safe-area Inset-Aware Header
        AgriCareHomeHeader(
            greeting = greeting,
            userName = stringResource(R.string.default_user_name),
            subline = stringResource(R.string.app_subline),
            hasUnreadNotifications = true,
            onNotificationClick = onNavigateToNotifications,
            onProfileClick = onNavigateToProfile,
            modifier = Modifier.testTag("home_screen_header")
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Search Bar
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .clickable(onClick = onNavigateToCropLibrary)
                        .testTag("home_search_bar"),
                    color = PureWhite,
                    border = BorderStroke(1.dp, OutlineLight),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.cd_search_action),
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2. Quick Action Shortcuts Row (Matching Mockup Screen 5: Diagnose, My Crops, Calendar, More)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_quick_actions_row"),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HomeShortcutItem(
                        icon = Icons.Default.CameraAlt,
                        label = stringResource(R.string.nav_diagnose),
                        iconTint = ForestGreenPrimary,
                        backgroundColor = SageSurface,
                        testTag = "home_shortcut_diagnose",
                        onClick = { onNavigateToDiagnose(true) }
                    )
                    HomeShortcutItem(
                        icon = Icons.Default.Eco,
                        label = stringResource(R.string.nav_my_crops),
                        iconTint = LeafGreenAccent,
                        backgroundColor = MintLight,
                        testTag = "home_shortcut_crops",
                        onClick = onNavigateToMyCrops
                    )
                    HomeShortcutItem(
                        icon = Icons.Default.CalendarToday,
                        label = stringResource(R.string.nav_calendar),
                        iconTint = Color(0xFF3B82F6),
                        backgroundColor = Color(0xFFEFF6FF),
                        testTag = "home_shortcut_calendar",
                        onClick = onNavigateToCalendar
                    )
                    HomeShortcutItem(
                        icon = Icons.Default.GridView,
                        label = stringResource(R.string.home_shortcut_more),
                        iconTint = ForestGreenDeep,
                        backgroundColor = Color(0xFFF1F5F3),
                        testTag = "home_shortcut_more",
                        onClick = onNavigateToProfile
                    )
                }
            }

            // 3. Primary Focal Action: Diagnose a Plant Card
            item {
                AgriCareCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_primary_diagnose_card"),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(SageSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.home_action_diagnose_plant),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = stringResource(R.string.app_tagline),
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Camera Action Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ForestGreenPrimary)
                                .clickable { onNavigateToDiagnose(true) }
                                .testTag("home_diagnose_camera_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.home_action_camera),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = PureWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        // Gallery Action Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, OutlineLight, RoundedCornerShape(14.dp))
                                .background(PureWhite)
                                .clickable { onNavigateToDiagnose(false) }
                                .testTag("home_diagnose_gallery_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = ForestGreenDeep,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.home_action_gallery),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = ForestGreenDeep,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. "Today's Tip" Card (Curated, sourced, with dismiss/refresh per PRD 6.3)
            item {
                AnimatedVisibility(
                    visible = isTipVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val tip = CURATED_TIPS[tipIndex % CURATED_TIPS.size]
                    AgriCareCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_todays_tip_card"),
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
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.home_section_todays_tip),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreenPrimary
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { tipIndex = (tipIndex + 1) % CURATED_TIPS.size },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.home_tip_refresh),
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { isTipVisible = false },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.home_tip_dismiss),
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp)
                            ) {
                                Text(
                                    text = tip.titleEn,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = tip.source,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Botanical leaf thumbnail preview
                            LeafTipThumbnail()
                        }
                    }
                }
            }

            // 5. "Recent Activity" Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_section_recent_activity),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = stringResource(R.string.action_view_all),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = ForestGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .clickable(onClick = onNavigateToHistory)
                            .padding(4.dp)
                            .testTag("home_recent_view_all")
                    )
                }
            }

            // Recent Diagnoses from Room Database or Honest Empty State
            if (diagnosisList.isEmpty()) {
                item {
                    AgriCareCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_recent_empty_card"),
                        backgroundColor = PureWhite,
                        borderColor = OutlineLight
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SageSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.home_recent_empty_title),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.home_recent_empty_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            } else {
                items(diagnosisList.take(3)) { diagnosis ->
                    RecentDiagnosisItem(
                        diagnosis = diagnosis,
                        onClick = onNavigateToHistory
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeShortcutItem(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    backgroundColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(width = 76.dp, height = 78.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PureWhite)
            .border(1.dp, OutlineLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RecentDiagnosisItem(
    diagnosis: DiagnosisEntity,
    onClick: () -> Unit
) {
    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("recent_diagnosis_item_${diagnosis.id}"),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leaf icon / photo container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SageSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = diagnosis.issueTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${diagnosis.cropGuess ?: "Plant"} · ${diagnosis.assessmentLevel}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
