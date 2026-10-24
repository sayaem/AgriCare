package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LeafGreenAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.SageSurface

/**
 * Onboarding Hero Visual 1: "Detect Plant Problems Early"
 * Organic curved card with young sprout emerging from soil and scanning ring.
 */
@Composable
fun OnboardingArtDetectPlantProblems(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5EB), Color(0xFFD6EEDC))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft radiant sun background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF9E6), Color(0x00FFFFFF)),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.45f
                ),
                radius = w * 0.45f,
                center = Offset(w * 0.5f, h * 0.35f)
            )

            // Rich soil hill
            val soilPath = Path().apply {
                moveTo(0f, h * 0.78f)
                cubicTo(w * 0.25f, h * 0.73f, w * 0.75f, h * 0.75f, w, h * 0.70f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = soilPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6B4E38), Color(0xFF4A3423))
                )
            )

            // Sprout stem
            val stemPath = Path().apply {
                moveTo(w * 0.5f, h * 0.75f)
                cubicTo(w * 0.49f, h * 0.60f, w * 0.51f, h * 0.48f, w * 0.5f, h * 0.40f)
            }
            drawPath(
                path = stemPath,
                color = Color(0xFF43A047),
                style = Stroke(width = 8f)
            )

            // Left sprout leaf
            val leftLeaf = Path().apply {
                moveTo(w * 0.5f, h * 0.52f)
                cubicTo(w * 0.30f, h * 0.48f, w * 0.22f, h * 0.34f, w * 0.32f, h * 0.30f)
                cubicTo(w * 0.44f, h * 0.32f, w * 0.48f, h * 0.45f, w * 0.5f, h * 0.52f)
                close()
            }
            drawPath(
                path = leftLeaf,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),
                    start = Offset(w * 0.32f, h * 0.30f),
                    end = Offset(w * 0.5f, h * 0.52f)
                )
            )

            // Right sprout leaf
            val rightLeaf = Path().apply {
                moveTo(w * 0.5f, h * 0.46f)
                cubicTo(w * 0.68f, h * 0.40f, w * 0.76f, h * 0.26f, w * 0.65f, h * 0.22f)
                cubicTo(w * 0.52f, h * 0.25f, w * 0.51f, h * 0.38f, w * 0.5f, h * 0.46f)
                close()
            }
            drawPath(
                path = rightLeaf,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF81C784), Color(0xFF388E3C)),
                    start = Offset(w * 0.65f, h * 0.22f),
                    end = Offset(w * 0.5f, h * 0.46f)
                )
            )

            // Top emerging bud
            val bud = Path().apply {
                moveTo(w * 0.5f, h * 0.40f)
                cubicTo(w * 0.46f, h * 0.30f, w * 0.54f, h * 0.30f, w * 0.5f, h * 0.40f)
                close()
            }
            drawPath(bud, color = Color(0xFFA5D6A7))

            // AI Scanner reticle ring (simulating early problem detection)
            drawCircle(
                color = Color(0xFF176B4D).copy(alpha = 0.25f),
                radius = w * 0.28f,
                center = Offset(w * 0.5f, h * 0.42f),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = Color(0xFF2EA86E).copy(alpha = 0.6f),
                radius = 10f,
                center = Offset(w * 0.38f, h * 0.36f)
            )
        }
    }
}

/**
 * Onboarding Hero Visual 2: "Expert Guidance at Your Fingertips"
 * Multiple botanical inspection pods with targeted leaf diagnosis.
 */
@Composable
fun OnboardingArtExpertGuidance(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEAF5EF), Color(0xFFD4EBDC))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Pod 1: Top left leaf
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2EA86E), Color(0xFF176B4D)),
                    start = Offset(w * 0.15f, h * 0.2f),
                    end = Offset(w * 0.48f, h * 0.55f)
                ),
                topLeft = Offset(w * 0.15f, h * 0.15f),
                size = Size(w * 0.35f, h * 0.45f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f)
            )

            // Pod 2: Top right leaf sample
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)),
                    start = Offset(w * 0.55f, h * 0.15f),
                    end = Offset(w * 0.85f, h * 0.48f)
                ),
                topLeft = Offset(w * 0.55f, h * 0.12f),
                size = Size(w * 0.32f, h * 0.35f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(35f, 35f)
            )

            // Pod 3: Bottom focal inspection pod
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE2F3E7), Color(0xFFC7E7CF)),
                    start = Offset(w * 0.3f, h * 0.52f),
                    end = Offset(w * 0.8f, h * 0.85f)
                ),
                topLeft = Offset(w * 0.35f, h * 0.52f),
                size = Size(w * 0.50f, h * 0.38f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(45f, 45f)
            )

            // Target reticle / diagnosis badge overlay
            drawCircle(
                color = Color(0xFF176B4D),
                radius = 18f,
                center = Offset(w * 0.60f, h * 0.68f)
            )
            drawCircle(
                color = Color.White,
                radius = 8f,
                center = Offset(w * 0.60f, h * 0.68f)
            )
        }
    }
}

/**
 * Onboarding Hero Visual 3: "For a Greener Tomorrow"
 * Serene golden hour agricultural field and farmer silhouette.
 */
@Composable
fun OnboardingArtGreenerTomorrow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFCBEAD7), Color(0xFFE2F4EA), Color(0xFFF7FBF8))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Distant soft hills
            val hill1 = Path().apply {
                moveTo(0f, h * 0.50f)
                cubicTo(w * 0.3f, h * 0.44f, w * 0.7f, h * 0.54f, w, h * 0.48f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(hill1, color = Color(0xFF81C784).copy(alpha = 0.45f))

            // Main crop field rows
            val field = Path().apply {
                moveTo(0f, h * 0.58f)
                cubicTo(w * 0.4f, h * 0.52f, w * 0.7f, h * 0.62f, w, h * 0.56f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = field,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                )
            )

            // Stylized crop rows
            for (i in 0..5) {
                val startX = w * (0.15f + i * 0.14f)
                val rowPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.56f)
                    lineTo(startX, h)
                }
                drawPath(
                    path = rowPath,
                    color = Color(0xFF43A047).copy(alpha = 0.4f),
                    style = Stroke(width = 4f)
                )
            }

            // Farmer figure in hat standing peacefully
            drawCircle(
                color = Color(0xFF20352B),
                radius = 16f,
                center = Offset(w * 0.65f, h * 0.46f) // Hat / head
            )
            drawOval(
                color = Color(0xFF20352B),
                topLeft = Offset(w * 0.60f, h * 0.45f),
                size = Size(w * 0.10f, 10f) // Hat brim
            )
            drawRoundRect(
                color = Color(0xFF176B4D),
                topLeft = Offset(w * 0.62f, h * 0.49f),
                size = Size(w * 0.06f, h * 0.14f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )
        }
    }
}

/**
 * Thumbnail for "Today's Tip" card (leaf with dew / inspection spot).
 */
@Composable
fun LeafTipThumbnail(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 68.dp, height = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2EA86E), Color(0xFF176B4D))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Center leaf vein
            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(w * 0.15f, h * 0.85f),
                end = Offset(w * 0.85f, h * 0.15f),
                strokeWidth = 3f
            )
            // Diagnostic leaf spot
            drawCircle(
                color = Color(0xFFC59B27).copy(alpha = 0.8f),
                radius = 8f,
                center = Offset(w * 0.50f, h * 0.45f)
            )
            drawCircle(
                color = Color(0xFF6B4E38),
                radius = 4f,
                center = Offset(w * 0.50f, h * 0.45f)
            )
        }
    }
}
