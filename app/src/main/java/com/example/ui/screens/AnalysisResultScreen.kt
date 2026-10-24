package com.example.ui.screens

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ai.AgriDiagnosisResult
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareOutlinedButton
import com.example.ui.components.AgriCarePill
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

@Composable
fun AnalysisResultScreen(
    diagnosisResult: AgriDiagnosisResult,
    imageUri: Uri,
    onViewTreatmentPlan: () -> Unit,
    onSaveToHistory: () -> Unit,
    onAddCrop: () -> Unit,
    onAnalyzeAnother: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSaved by remember { mutableStateOf(false) }

    val assessmentLabel = when (diagnosisResult.assessmentLevel) {
        "likely" -> "Likely match"
        "uncertain" -> "Needs more information"
        else -> "Uncertain diagnosis"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("analysis_result_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_analysis_result),
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack,
            actions = {
                IconButton(
                    onClick = {
                        if (!isSaved) {
                            onSaveToHistory()
                            isSaved = true
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("result_save_bookmark_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = stringResource(R.string.action_save),
                        tint = if (isSaved) ForestGreenPrimary else TextSecondary
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Thumbnail & Issue Title Card matching Mockup Screen 6
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
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            AgriCarePill(
                                text = assessmentLabel,
                                backgroundColor = MintLight,
                                textColor = ForestGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = diagnosisResult.issueTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Crop: ${diagnosisResult.cropGuess ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // 2. Visible Symptoms Section
            if (diagnosisResult.visibleSymptoms.isNotEmpty()) {
                item {
                    AgriCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = PureWhite,
                        borderColor = OutlineLight
                    ) {
                        Text(
                            text = "Key Symptoms",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        diagnosisResult.visibleSymptoms.forEach { symptom ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ForestGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = symptom,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Alternative Causes
            if (diagnosisResult.alternativeCauses.isNotEmpty()) {
                item {
                    AgriCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = PureWhite,
                        borderColor = OutlineLight
                    ) {
                        Text(
                            text = "Also Possible / Alternative Causes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        diagnosisResult.alternativeCauses.forEach { cause ->
                            Text(
                                text = "• $cause",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // 4. Escalation Advice & Disclaimer (PRD 6.8 & 7.2)
            item {
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SageSurface,
                    borderColor = OutlineLight
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ForestGreenDeep,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Agricultural Advisory Note",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDeep
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = diagnosisResult.escalationAdvice ?: "Image-based suggestions are not a definitive laboratory diagnosis. Consult your local Upazila Agriculture Officer when uncertain.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            // 5. Action Buttons (View Treatment Plan, Save to History, Analyze Another)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AgriCarePrimaryButton(
                        text = "View Treatment Plan",
                        onClick = onViewTreatmentPlan,
                        modifier = Modifier.testTag("result_view_treatment_btn")
                    )

                    if (!isSaved) {
                        AgriCareOutlinedButton(
                            text = "Save to History",
                            onClick = {
                                onSaveToHistory()
                                isSaved = true
                            },
                            modifier = Modifier.testTag("result_save_history_btn")
                        )
                    }

                    AgriCareOutlinedButton(
                        text = "Analyze Another Image",
                        onClick = onAnalyzeAnother,
                        modifier = Modifier.testTag("result_analyze_another_btn")
                    )
                }
            }
        }
    }
}
