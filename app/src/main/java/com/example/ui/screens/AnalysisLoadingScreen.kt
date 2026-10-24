package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ai.AgriDiagnosisResult
import com.example.data.ai.GeminiRepository
import com.example.ui.components.AgriCareOutlinedButton
import com.example.ui.components.AgriCarePrimaryButton
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.theme.AlertError
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import com.example.util.AppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnalysisLoadingScreen(
    imageUri: Uri,
    userNotes: String?,
    currentLanguage: AppLanguage,
    onAnalysisSuccess: (AgriDiagnosisResult, Uri) -> Unit,
    onAnalysisError: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { GeminiRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRetrying by remember { mutableStateOf(false) }

    val steps = listOf(
        "Preparing image & resizing...",
        "Examining visible plant symptoms...",
        "Consulting agricultural guidance database..."
    )

    LaunchedEffect(imageUri, isRetrying) {
        val stepJob = coroutineScope.launch {
            while (currentStepIndex < steps.size - 1) {
                delay(1200)
                currentStepIndex++
            }
        }

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) {
                errorMessage = "Failed to load image from URI."
                onAnalysisError("Failed to load image from URI.")
                stepJob.cancel()
                return@LaunchedEffect
            }

            val langCode = if (currentLanguage == AppLanguage.BANGLA) "bn-BD" else "en"
            val result = repository.analyzePlantImage(bitmap, langCode, userNotes)

            stepJob.cancel()

            result.fold(
                onSuccess = { diagnosis ->
                    onAnalysisSuccess(diagnosis, imageUri)
                },
                onFailure = { err ->
                    errorMessage = err.message ?: "An unexpected error occurred during AI analysis."
                }
            )
        } catch (e: Exception) {
            stepJob.cancel()
            errorMessage = e.message ?: "Failed to process image."
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("analysis_loading_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AgriCareTopBar(
            title = "Analyzing Plant",
            navigationIcon = NavigationIconType.CLOSE,
            onNavigationClick = onCancel
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(AlertError.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = AlertError,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Analysis Failed",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = errorMessage ?: "Unknown error",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 22.sp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                AgriCarePrimaryButton(
                    text = stringResource(R.string.action_retry),
                    onClick = {
                        errorMessage = null
                        currentStepIndex = 0
                        isRetrying = !isRetrying
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                AgriCareOutlinedButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = onCancel
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = ForestGreenPrimary,
                    strokeWidth = 4.dp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = steps[currentStepIndex],
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gemini Vision is examining visible evidence cautiously. Please wait a moment.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                AgriCareOutlinedButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }
    }
}
