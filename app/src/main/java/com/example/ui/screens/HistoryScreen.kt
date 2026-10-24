package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.AgriCareEmptyState
import com.example.ui.components.AgriCarePill
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.theme.AlertError
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    diagnosisDao: DiagnosisDao,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val allDiagnoses by diagnosisDao.getAllDiagnoses().collectAsState(initial = emptyList())

    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Diagnoses, 2: Treatments
    var showClearConfirm by remember { mutableStateOf(false) }
    var selectedDiagnosisForDetail by remember { mutableStateOf<DiagnosisEntity?>(null) }
    var diagnosisToDelete by remember { mutableStateOf<DiagnosisEntity?>(null) }

    val filterLabels = listOf(
        stringResource(R.string.history_filter_all),
        stringResource(R.string.history_filter_diagnoses),
        stringResource(R.string.history_filter_treatments)
    )

    val filteredList = when (selectedFilterIndex) {
        1 -> allDiagnoses.filter { it.status == "completed" }
        2 -> allDiagnoses.filter { it.immediateSteps.isNotEmpty() }
        else -> allDiagnoses
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("history_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_history),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack,
            actions = {
                if (allDiagnoses.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("history_clear_all_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(R.string.history_clear_all),
                            tint = TextSecondary
                        )
                    }
                }
            }
        )

        // Filter Pills matching mockup Screen 11
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterLabels.forEachIndexed { index, label ->
                val isSelected = selectedFilterIndex == index
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ForestGreenPrimary else PureWhite)
                        .border(1.dp, if (isSelected) ForestGreenPrimary else OutlineLight, RoundedCornerShape(12.dp))
                        .clickable { selectedFilterIndex = index }
                        .padding(horizontal = 16.dp)
                        .testTag("history_filter_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) PureWhite else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }

        // List or Honest Empty State
        if (filteredList.isEmpty()) {
            AgriCareEmptyState(
                title = stringResource(R.string.empty_history_title),
                description = stringResource(R.string.empty_history_desc),
                icon = Icons.Default.History,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { diagnosis ->
                    HistoryItemCard(
                        diagnosis = diagnosis,
                        onClick = { selectedDiagnosisForDetail = diagnosis }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedDiagnosisForDetail?.let { diagnosis ->
        DiagnosisDetailDialog(
            diagnosis = diagnosis,
            onDismiss = { selectedDiagnosisForDetail = null },
            onDelete = {
                selectedDiagnosisForDetail = null
                diagnosisToDelete = diagnosis
            }
        )
    }

    // Single Delete Confirmation
    diagnosisToDelete?.let { diagnosis ->
        AlertDialog(
            onDismissRequest = { diagnosisToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.action_delete),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this diagnosis record?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            diagnosisDao.deleteDiagnosis(diagnosis)
                        }
                        diagnosisToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AlertError)
                ) {
                    Text(stringResource(R.string.action_delete), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { diagnosisToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Clear All Confirmation (PRD 6.14: Clear all history only after confirmation)
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = {
                Text(
                    text = stringResource(R.string.history_clear_confirm_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.history_clear_confirm_msg),
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            diagnosisDao.clearAllDiagnoses()
                        }
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AlertError),
                    modifier = Modifier.testTag("confirm_clear_history_btn")
                ) {
                    Text(stringResource(R.string.history_clear_all), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    diagnosis: DiagnosisEntity,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
    val dateStr = dateFormat.format(Date(diagnosis.createdAt))

    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("history_item_${diagnosis.id}"),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MintLight),
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
                    text = diagnosis.issueTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${diagnosis.cropGuess ?: "Plant"} · $dateStr",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                AgriCarePill(
                    text = diagnosis.assessmentLevel,
                    backgroundColor = SageSurface,
                    textColor = ForestGreenDeep
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

@Composable
private fun DiagnosisDetailDialog(
    diagnosis: DiagnosisEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy, h:mm a", Locale.getDefault()) }
    val dateStr = dateFormat.format(Date(diagnosis.createdAt))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = diagnosis.issueTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${diagnosis.cropGuess ?: "Crop"} · $dateStr",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (diagnosis.symptoms.isNotEmpty()) {
                    item {
                        Text(
                            text = "Observed Symptoms:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        diagnosis.symptoms.forEach { symptom ->
                            Text(
                                text = "• $symptom",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
                if (diagnosis.immediateSteps.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recommended Steps:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        diagnosis.immediateSteps.forEach { step ->
                            Text(
                                text = "• $step",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
                if (!diagnosis.escalationAdvice.isNullOrBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Escalation Advice:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = diagnosis.escalationAdvice,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = AlertError)
                ) {
                    Text(stringResource(R.string.action_delete))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_close))
                }
            }
        }
    )
}
