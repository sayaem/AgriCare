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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ai.TreatmentStepDto
import com.example.ui.components.AgriCareCard
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
fun TreatmentPlanScreen(
    steps: List<TreatmentStepDto>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Local completion state map for checklist steps
    val completedState = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("treatment_plan_screen")
    ) {
        AgriCareTopBar(
            title = stringResource(R.string.title_treatment_plan),
            subtitle = "Actionable Control & Prevention Checklist",
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Numbered Priority Actions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            if (steps.isEmpty()) {
                item {
                    AgriCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = PureWhite,
                        borderColor = OutlineLight
                    ) {
                        Text(
                            text = "Standard cultural practices apply: Remove infected plant debris, avoid overhead leaf wetting, and maintain balanced soil nutrition.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                        )
                    }
                }
            } else {
                itemsIndexed(steps) { index, step ->
                    val isDone = completedState[index] == true

                    AgriCareCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                completedState[index] = !isDone
                            }
                            .testTag("treatment_step_$index"),
                        backgroundColor = if (isDone) SageSurface.copy(alpha = 0.5f) else PureWhite,
                        borderColor = if (isDone) ForestGreenPrimary.copy(alpha = 0.5f) else OutlineLight
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Step Number Circle or Checkmark
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) ForestGreenPrimary else MintLight),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreenPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) TextSecondary else TextPrimary,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Safety Reminder Card (PRD 6.10)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                AgriCareCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SageSurface,
                    borderColor = OutlineLight
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = ForestGreenDeep,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Safety & Label Reminder",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDeep
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Always follow local product labels, wear required protective equipment (PPE), keep children and animals away, and prioritize low-risk cultural controls first.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
