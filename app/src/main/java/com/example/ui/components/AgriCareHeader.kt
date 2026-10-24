package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite

enum class NavigationIconType {
    NONE,
    BACK,
    CLOSE
}

/**
 * Standard Inset-Aware Top Bar for AgriCare.
 * Adheres strictly to Section 4 of Master PRD:
 * - Begins below status bar using statusBarsPadding() without manual Y offsets or double-padding.
 * - Exactly 56.dp content area height.
 * - 20.dp horizontal content padding aligned with dashboard cards.
 * - Leading icon, title, and action icons share visually consistent centerline.
 * - Minimum 48.dp touch targets on all interactive actions.
 */
@Composable
fun AgriCareTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: NavigationIconType = NavigationIconType.BACK,
    onNavigationClick: (() -> Unit)? = null,
    backgroundColor: Color = WarmOffWhite,
    contentColor: Color = TextPrimary,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("agricare_top_bar"),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != NavigationIconType.NONE && onNavigationClick != null) {
                IconButton(
                    onClick = onNavigationClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("top_bar_nav_button")
                ) {
                    val icon: ImageVector = when (navigationIcon) {
                        NavigationIconType.CLOSE -> Icons.Default.Close
                        else -> Icons.AutoMirrored.Filled.ArrowBack
                    }
                    val cd = when (navigationIcon) {
                        NavigationIconType.CLOSE -> stringResource(R.string.action_close)
                        else -> stringResource(R.string.action_back)
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = cd,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .testTag("top_bar_title_container"),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("top_bar_title")
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("top_bar_subtitle")
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                content = actions
            )
        }
    }
}

/**
 * Expressive Home Dashboard Header matching the Master Visual Mockup:
 * - Time-aware greeting ("Good Morning, Sayed" / localized) with sun/greeting icon.
 * - Subline ("Healthy plants, happier harvests 🌱").
 * - Action icons (Notifications & Profile avatar) with 48.dp touch targets.
 * - Perfectly aligned with 20.dp screen horizontal padding.
 */
@Composable
fun AgriCareHomeHeader(
    greeting: String,
    userName: String,
    subline: String,
    modifier: Modifier = Modifier,
    hasUnreadNotifications: Boolean = true,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("agricare_home_header"),
        color = WarmOffWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = Color(0xFFF4A261),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$greeting $userName",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("home_greeting_text")
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subline,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notifications button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.cd_notifications_bell),
                            onClick = onNotificationClick
                        )
                        .testTag("home_notification_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        Icon(
                            imageVector = if (hasUnreadNotifications) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                            contentDescription = stringResource(R.string.cd_notifications_bell),
                            tint = ForestGreenDeep,
                            modifier = Modifier.size(24.dp)
                        )
                        if (hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color(0xFFE76F51), CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Profile avatar button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(R.string.cd_profile_avatar),
                            onClick = onProfileClick
                        )
                        .testTag("home_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SageSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}
