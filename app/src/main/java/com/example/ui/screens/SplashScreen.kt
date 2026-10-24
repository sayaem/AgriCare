package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextOnDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoaded: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.92f) }

    LaunchedEffect(isLoaded) {
        // Quick, calm brand splash per PRD 6.1 (under 1.3 seconds, no forced long delays)
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        delay(700)
        // Ensure preferences have finished loading from DataStore before dismissing splash
        while (!isLoaded) {
            delay(50)
        }
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F3626), // Deep botanical forest night
                        Color(0xFF176B4D), // Primary AgriCare forest green
                        Color(0xFF0C2B1E)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("agricare_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Background botanical texture
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft radiant glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3343A047), Color(0x00000000)),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * 0.5f, h * 0.45f)
            )

            // Stylized giant leaf background silhouette
            val leafBg = Path().apply {
                moveTo(w * 0.1f, h * 0.2f)
                cubicTo(w * 0.8f, h * 0.1f, w * 0.95f, h * 0.7f, w * 0.3f, h * 0.9f)
            }
            drawPath(
                path = leafBg,
                color = Color.White.copy(alpha = 0.04f),
                style = Stroke(width = 4f)
            )
        }

        // Center Content: Leaf Logo, Wordmark, Tagline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Elegant twin leaf vector mark matching mockup Screen 1
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .testTag("splash_logo_icon"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Main standing leaf
                    val mainLeaf = Path().apply {
                        moveTo(w * 0.52f, h * 0.12f)
                        cubicTo(w * 0.85f, h * 0.22f, w * 0.90f, h * 0.65f, w * 0.52f, h * 0.88f)
                        cubicTo(w * 0.48f, h * 0.65f, w * 0.50f, h * 0.35f, w * 0.52f, h * 0.12f)
                        close()
                    }
                    drawPath(
                        path = mainLeaf,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE8F5E9), Color(0xFFA5D6A7)),
                            start = Offset(w * 0.52f, h * 0.12f),
                            end = Offset(w * 0.52f, h * 0.88f)
                        )
                    )

                    // Secondary offset companion leaf
                    val smallLeaf = Path().apply {
                        moveTo(w * 0.48f, h * 0.40f)
                        cubicTo(w * 0.15f, h * 0.42f, w * 0.12f, h * 0.76f, w * 0.48f, h * 0.88f)
                        cubicTo(w * 0.44f, h * 0.72f, w * 0.46f, h * 0.54f, w * 0.48f, h * 0.40f)
                        close()
                    }
                    drawPath(
                        path = smallLeaf,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFC8E6C9), Color(0xFF81C784)),
                            start = Offset(w * 0.48f, h * 0.40f),
                            end = Offset(w * 0.48f, h * 0.88f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnDark,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.testTag("splash_app_name")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Healthy Plants\nBetter Harvests",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFD4EBDC),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
        }

        // Bottom subtle footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Calm capsule indicator bar
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 4.dp)
                    .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Powered by AI · Built for Farmers",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            )
        }
    }
}
