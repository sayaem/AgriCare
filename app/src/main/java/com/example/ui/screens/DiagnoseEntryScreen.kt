package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AgriCareCard
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
import java.io.File
import java.io.FileOutputStream

@Composable
fun DiagnoseEntryScreen(
    onImageSelected: (Uri, String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var userNotes by remember { mutableStateOf("") }
    var consentGiven by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri, userNotes.trim().ifEmpty { null })
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToCache(context, bitmap)
            if (uri != null) {
                onImageSelected(uri, userNotes.trim().ifEmpty { null })
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            android.widget.Toast.makeText(
                context,
                "Camera permission is required to take plant photos. Please enable it in system settings.",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("diagnose_entry_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_diagnose),
            subtitle = "Camera / Gallery AI Inspection",
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AgriCareCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diagnose_actions_card"),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Text(
                        text = "Choose Inspection Source",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (consentGiven) ForestGreenPrimary else ForestGreenPrimary.copy(alpha = 0.4f))
                                .clickable(enabled = consentGiven) {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                                .testTag("entry_camera_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(20.dp)
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

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, OutlineLight, RoundedCornerShape(16.dp))
                                .background(PureWhite)
                                .clickable(enabled = consentGiven) {
                                    galleryLauncher.launch("image/*")
                                }
                                .testTag("entry_gallery_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = ForestGreenDeep,
                                    modifier = Modifier.size(20.dp)
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

                    if (!consentGiven) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Please accept the upload consent disclosure below to enable camera and gallery capture.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD97706), fontSize = 12.sp)
                        )
                    }
                }
            }

            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Text(
                        text = "Optional Crop Context",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mention crop name, age, watering, or recent weather to improve AI accuracy.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = userNotes,
                        onValueChange = { userNotes = it },
                        placeholder = { Text("e.g. Rice, 45 days old, leaves yellowing after rain") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreenPrimary,
                            unfocusedBorderColor = OutlineLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("entry_notes_input")
                    )
                }
            }

            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SageSurface,
                    borderColor = OutlineLight
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = consentGiven,
                            onCheckedChange = { consentGiven = it },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = ForestGreenPrimary
                            ),
                            modifier = Modifier.testTag("entry_consent_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I understand that the selected photo and notes will be sent to Gemini AI for processing to generate agricultural guidance.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = PureWhite,
                    borderColor = OutlineLight
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFF4A261),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Photography Tips",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PhotographyTipRow("Focus sharply on the affected leaf area or spots.")
                    PhotographyTipRow("Use bright natural daylight; avoid blurry flash photos.")
                    PhotographyTipRow("Include both diseased spots and healthy surrounding tissue.")
                }
            }
        }
    }
}

@Composable
private fun PhotographyTipRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(ForestGreenPrimary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val file = File(context.cacheDir, "agricare_capture_${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
        fos.flush()
        fos.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        null
    }
}
