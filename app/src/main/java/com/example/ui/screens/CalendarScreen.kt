package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.CropTaskDao
import com.example.data.local.CropTaskEntity
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareEmptyState
import com.example.ui.components.AgriCarePrimaryButton
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import com.example.util.NotificationHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DateItem(
    val dayOfWeek: String,
    val dayNumber: Int,
    val dateString: String,
    val timeInMillis: Long
)

@Composable
fun CalendarScreen(
    taskDao: CropTaskDao,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tasks by taskDao.getAllTasks().collectAsState(initial = emptyList())

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<CropTaskEntity?>(null) }

    // Permission launcher for Android 13+ notifications
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    // Generate upcoming dates strip (e.g. 14 days around today)
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())

    val dateList = remember {
        val list = mutableListOf<DateItem>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -3)
        for (i in 0..14) {
            val dow = dayOfWeekFormat.format(cal.time).uppercase()
            val dayNum = cal.get(Calendar.DAY_OF_MONTH)
            val ds = dateFormat.format(cal.time)
            list.add(DateItem(dow, dayNum, ds, cal.timeInMillis))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    var selectedDateStr by remember {
        mutableStateOf(dateFormat.format(Calendar.getInstance().time))
    }

    // Filter tasks for selected date
    val todaysTasks = tasks.filter { task ->
        dateFormat.format(Date(task.dueAt)) == selectedDateStr
    }
    val upcomingTasks = tasks.filter { task ->
        task.dueAt > System.currentTimeMillis() && dateFormat.format(Date(task.dueAt)) != selectedDateStr
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("calendar_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_calendar),
            subtitle = "November 2024 Crop Tasks & Reminders",
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack,
            actions = {
                IconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        taskToEdit = null
                        showAddTaskDialog = true
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("calendar_add_task_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add),
                        tint = ForestGreenPrimary
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Horizontal Date Strip (Mockup Ref 6.13)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(dateList) { dateItem ->
                            val isSelected = dateItem.dateString == selectedDateStr
                            Surface(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedDateStr = dateItem.dateString }
                                    .testTag("date_item_${dateItem.dayNumber}"),
                                color = if (isSelected) ForestGreenPrimary else PureWhite,
                                shape = RoundedCornerShape(16.dp),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dateItem.dayOfWeek,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) MintLight else TextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${dateItem.dayNumber}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = if (isSelected) PureWhite else TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Today's Tasks Section
            item {
                Text(
                    text = "Selected Date Tasks (${todaysTasks.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            if (todaysTasks.isEmpty()) {
                item {
                    AgriCareEmptyState(
                        title = stringResource(R.string.empty_tasks_title),
                        description = stringResource(R.string.empty_tasks_desc),
                        icon = Icons.Default.NotificationsActive,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(todaysTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = {
                            coroutineScope.launch {
                                val newDone = !task.isCompleted
                                taskDao.updateTask(
                                    task.copy(
                                        isCompleted = newDone,
                                        completedAt = if (newDone) System.currentTimeMillis() else null
                                    )
                                )
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                taskDao.deleteTask(task)
                            }
                        },
                        onEdit = {
                            taskToEdit = task
                            showAddTaskDialog = true
                        }
                    )
                }
            }

            // Upcoming Section
            if (upcomingTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upcoming Tasks",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                items(upcomingTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = {
                            coroutineScope.launch {
                                val newDone = !task.isCompleted
                                taskDao.updateTask(
                                    task.copy(
                                        isCompleted = newDone,
                                        completedAt = if (newDone) System.currentTimeMillis() else null
                                    )
                                )
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                taskDao.deleteTask(task)
                            }
                        },
                        onEdit = {
                            taskToEdit = task
                            showAddTaskDialog = true
                        }
                    )
                }
            }
        }
    }

    // Add / Edit Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            existingTask = taskToEdit,
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, notes, dueAt, notificationEnabled ->
                coroutineScope.launch {
                    if (taskToEdit != null) {
                        taskDao.updateTask(
                            taskToEdit!!.copy(
                                title = title,
                                notes = notes,
                                dueAt = dueAt,
                                notificationEnabled = notificationEnabled,
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                    } else {
                        val newTask = CropTaskEntity(
                            title = title,
                            notes = notes,
                            dueAt = dueAt,
                            notificationEnabled = notificationEnabled
                        )
                        taskDao.insertTask(newTask)
                        if (notificationEnabled) {
                            NotificationHelper.showTaskNotification(
                                context,
                                newTask.id,
                                "Task Scheduled: $title",
                                notes ?: "Your crop task is scheduled."
                            )
                        }
                    }
                    showAddTaskDialog = false
                    taskToEdit = null
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: CropTaskEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(task.dueAt))

    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .testTag("task_card_${task.id}"),
        backgroundColor = if (task.isCompleted) SageSurface.copy(alpha = 0.5f) else PureWhite,
        borderColor = if (task.isCompleted) ForestGreenPrimary.copy(alpha = 0.5f) else OutlineLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) ForestGreenPrimary else MintLight)
                    .clickable { onToggleComplete() }
                    .testTag("task_checkbox_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) TextSecondary else TextPrimary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.notes ?: "Crop management task"} • $timeFormatted",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("task_delete_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    existingTask: CropTaskEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String?, Long, Boolean) -> Unit
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
    var notificationEnabled by remember { mutableStateOf(existingTask?.notificationEnabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingTask == null) "Add Crop Task / Reminder" else "Edit Crop Task",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title (e.g. Apply Fertilizer)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        focusedLabelColor = ForestGreenPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_task_title_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Instructions (optional)") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        focusedLabelColor = ForestGreenPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_task_notes_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Enable Notification Reminder",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                    )
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { notificationEnabled = it },
                        modifier = Modifier.testTag("dialog_task_notification_switch")
                    )
                }
            }
        },
        confirmButton = {
            AgriCarePrimaryButton(
                text = if (existingTask == null) "Create Task" else "Save Changes",
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, notes.ifBlank { null }, System.currentTimeMillis() + 86400000L, notificationEnabled)
                    }
                },
                modifier = Modifier.testTag("dialog_task_save_btn")
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_task_cancel_btn")
            ) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp)
    )
}
