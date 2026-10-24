package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.AgriCareDestination
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LeafGreenAccent
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary

/**
 * Bottom Navigation Bar reflecting the approved Master Visual Mockup:
 * - 5 Destinations: Home, History, Diagnose (elevated central action), Calendar, Profile.
 * - Central elevated circular FAB button with leaf/camera icon.
 * - Strict safe-area insets via navigationBarsPadding() so navigation bars or gesture bars never overlap.
 * - Test tags and 48dp minimum touch targets.
 */
@Composable
fun AgriCareBottomBar(
    currentDestination: AgriCareDestination,
    onNavigate: (AgriCareDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("agricare_bottom_bar"),
        color = PureWhite,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItem(
                    label = stringResource(R.string.nav_home),
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = currentDestination == AgriCareDestination.HOME,
                    testTag = "bottom_nav_home",
                    onClick = { onNavigate(AgriCareDestination.HOME) }
                )

                // 2. History
                BottomNavItem(
                    label = stringResource(R.string.nav_history),
                    selectedIcon = Icons.Filled.History,
                    unselectedIcon = Icons.Outlined.History,
                    isSelected = currentDestination == AgriCareDestination.HISTORY,
                    testTag = "bottom_nav_history",
                    onClick = { onNavigate(AgriCareDestination.HISTORY) }
                )

                // Spacer for central elevated Diagnose button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("bottom_nav_diagnose_placeholder")
                )

                // 4. Calendar
                BottomNavItem(
                    label = stringResource(R.string.nav_calendar),
                    selectedIcon = Icons.Filled.CalendarToday,
                    unselectedIcon = Icons.Outlined.CalendarToday,
                    isSelected = currentDestination == AgriCareDestination.CALENDAR,
                    testTag = "bottom_nav_calendar",
                    onClick = { onNavigate(AgriCareDestination.CALENDAR) }
                )

                // 5. Profile
                BottomNavItem(
                    label = stringResource(R.string.nav_profile),
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    isSelected = currentDestination == AgriCareDestination.PROFILE,
                    testTag = "bottom_nav_profile",
                    onClick = { onNavigate(AgriCareDestination.PROFILE) }
                )
            }

            // Central Elevated Diagnose Action Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-14).dp)
                    .size(56.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(ForestGreenPrimary)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(R.string.cd_diagnose_button),
                        onClick = { onNavigate(AgriCareDestination.DIAGNOSE) }
                    )
                    .testTag("bottom_nav_diagnose_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Eco,
                    contentDescription = stringResource(R.string.cd_diagnose_button),
                    tint = PureWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = ForestGreenPrimary
    val inactiveColor = TextSecondary

    Column(
        modifier = modifier
            .size(width = 64.dp, height = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 28.dp),
                role = Role.Tab,
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) activeColor else inactiveColor
            ),
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
