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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.local.PlantDao
import com.example.data.local.PlantEntity
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareEmptyState
import com.example.ui.components.AgriCarePrimaryButton
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
fun MyCropsScreen(
    plantDao: PlantDao,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val activePlants by plantDao.getAllActivePlants().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Crops, 1: All Crops
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPlantForDetail by remember { mutableStateOf<PlantEntity?>(null) }
    var plantToEdit by remember { mutableStateOf<PlantEntity?>(null) }
    var plantToDelete by remember { mutableStateOf<PlantEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("my_crops_screen")
    ) {
        // Safe-area Top Bar matching Mockup Screen 9
        AgriCareTopBar(
            title = stringResource(R.string.title_my_crops),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack,
            actions = {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showAddDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("my_crops_add_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.action_add),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add),
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )

        // Segmented Tabs: "My Crops" / "All Crops"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE5EDE8))
                .padding(3.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selectedTab == 0) ForestGreenPrimary else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .testTag("my_crops_tab_my"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.my_crops_tab_my_crops),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selectedTab == 0) PureWhite else TextSecondary,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selectedTab == 1) ForestGreenPrimary else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .testTag("my_crops_tab_all"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.my_crops_tab_all_crops),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (selectedTab == 1) PureWhite else TextSecondary,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }

        // Crops List or Empty State
        if (activePlants.isEmpty()) {
            AgriCareEmptyState(
                title = stringResource(R.string.empty_crops_title),
                description = stringResource(R.string.empty_crops_desc),
                icon = Icons.Default.Eco,
                actionButton = {
                    AgriCarePrimaryButton(
                        text = stringResource(R.string.crop_add_title),
                        onClick = { showAddDialog = true },
                        leadingIcon = Icons.Default.Add,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .testTag("empty_crops_add_btn")
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activePlants, key = { it.id }) { plant ->
                    PlantRowItem(
                        plant = plant,
                        onClick = { selectedPlantForDetail = plant }
                    )
                }
            }
        }
    }

    // Add or Edit Crop Dialog
    if (showAddDialog || plantToEdit != null) {
        val editing = plantToEdit
        AddEditCropDialog(
            initialPlant = editing,
            onDismiss = {
                showAddDialog = false
                plantToEdit = null
            },
            onSave = { plant ->
                coroutineScope.launch {
                    if (editing != null) {
                        plantDao.updatePlant(plant)
                    } else {
                        plantDao.insertPlant(plant)
                    }
                }
                showAddDialog = false
                plantToEdit = null
            }
        )
    }

    // Crop Detail Dialog
    selectedPlantForDetail?.let { plant ->
        CropDetailDialog(
            plant = plant,
            onDismiss = { selectedPlantForDetail = null },
            onEdit = {
                selectedPlantForDetail = null
                plantToEdit = plant
            },
            onDelete = {
                selectedPlantForDetail = null
                plantToDelete = plant
            }
        )
    }

    // Delete Confirmation Dialog (PRD 9.2: Explicit confirmation for deletion)
    plantToDelete?.let { plant ->
        AlertDialog(
            onDismissRequest = { plantToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.crop_delete_confirm_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.crop_delete_confirm_msg),
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            plantDao.deletePlant(plant)
                        }
                        plantToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AlertError),
                    modifier = Modifier.testTag("confirm_delete_crop_btn")
                ) {
                    Text(stringResource(R.string.action_delete), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { plantToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun PlantRowItem(
    plant: PlantEntity,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
    val plantedDateStr = plant.plantedAt?.let { dateFormat.format(Date(it)) } ?: "Recent"

    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("plant_item_${plant.id}"),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botanical icon / thumbnail pod
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
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${stringResource(R.string.crop_label_variety)}: ${plant.variety ?: "Local"}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${stringResource(R.string.crop_label_planted)}: $plantedDateStr",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
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

@Composable
private fun AddEditCropDialog(
    initialPlant: PlantEntity?,
    onDismiss: () -> Unit,
    onSave: (PlantEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialPlant?.name ?: "") }
    var cropType by remember { mutableStateOf(initialPlant?.cropType ?: "Rice") }
    var variety by remember { mutableStateOf(initialPlant?.variety ?: "") }
    var fieldLabel by remember { mutableStateOf(initialPlant?.fieldLabel ?: "") }
    var notes by remember { mutableStateOf(initialPlant?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialPlant == null) stringResource(R.string.crop_add_title) else stringResource(R.string.crop_edit_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.crop_name_hint)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = OutlineLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("crop_name_input")
                )

                OutlinedTextField(
                    value = variety,
                    onValueChange = { variety = it },
                    label = { Text(stringResource(R.string.crop_variety_hint)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = OutlineLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("crop_variety_input")
                )

                OutlinedTextField(
                    value = fieldLabel,
                    onValueChange = { fieldLabel = it },
                    label = { Text(stringResource(R.string.crop_field_hint)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = OutlineLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("crop_field_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.crop_notes_hint)) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = OutlineLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("crop_notes_input")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val plant = initialPlant?.copy(
                            name = name.trim(),
                            cropType = name.trim(),
                            variety = variety.trim().ifEmpty { null },
                            fieldLabel = fieldLabel.trim().ifEmpty { null },
                            notes = notes.trim().ifEmpty { null },
                            updatedAt = System.currentTimeMillis()
                        ) ?: PlantEntity(
                            name = name.trim(),
                            cropType = name.trim(),
                            variety = variety.trim().ifEmpty { null },
                            fieldLabel = fieldLabel.trim().ifEmpty { null },
                            notes = notes.trim().ifEmpty { null },
                            plantedAt = System.currentTimeMillis()
                        )
                        onSave(plant)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("save_crop_btn")
            ) {
                Text(stringResource(R.string.action_save), fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun CropDetailDialog(
    plant: PlantEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy", Locale.getDefault()) }
    val plantedDateStr = plant.plantedAt?.let { dateFormat.format(Date(it)) } ?: "Recorded"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${stringResource(R.string.crop_label_variety)}: ${plant.variety ?: "Local"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stringResource(R.string.crop_label_planted)}: $plantedDateStr",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!plant.fieldLabel.isNullOrBlank()) {
                    Text(
                        text = "${stringResource(R.string.crop_label_field)}: ${plant.fieldLabel}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (!plant.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stringResource(R.string.crop_label_notes)}:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = plant.notes,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
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
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.action_edit), color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}
