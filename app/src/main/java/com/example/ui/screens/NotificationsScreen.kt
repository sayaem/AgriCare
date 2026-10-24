package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.CropTaskDao
import com.example.data.local.CropTaskEntity
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareEmptyState
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    taskDao: CropTaskDao,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by taskDao.getAllTasks().collectAsState(initial = emptyList())
    val activeReminders = tasks.filter { !it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("notifications_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_notifications),
            subtitle = "Crop Reminders & Alerts",
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (activeReminders.isEmpty()) {
                item {
                    AgriCareEmptyState(
                        title = stringResource(R.string.empty_notifications_title),
                        description = stringResource(R.string.empty_notifications_desc),
                        icon = Icons.Default.Notifications,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                items(activeReminders) { task ->
                    NotificationCard(task = task)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(task: CropTaskEntity) {
    val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(task.dueAt))

    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_card_${task.id}"),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = ForestGreenPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.notes ?: "Scheduled task reminder",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ForestGreenDeep,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
