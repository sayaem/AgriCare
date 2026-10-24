package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AgriCarePrimaryButton
import com.example.ui.components.OnboardingArtDetectPlantProblems
import com.example.ui.components.OnboardingArtExpertGuidance
import com.example.ui.components.OnboardingArtGreenerTomorrow
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite
import com.example.util.AppLanguage

data class OnboardingSlideData(
    val titleRes: Int,
    val descriptionRes: Int
)

@Composable
fun OnboardingScreen(
    onCompleteOnboarding: () -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlide by remember { mutableIntStateOf(0) }

    val slides = listOf(
        OnboardingSlideData(R.string.onboarding_title_1, R.string.onboarding_desc_1),
        OnboardingSlideData(R.string.onboarding_title_2, R.string.onboarding_desc_2),
        OnboardingSlideData(R.string.onboarding_title_3, R.string.onboarding_desc_3)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .testTag("onboarding_screen")
    ) {
        // Top Action Bar: Language switch on left + Skip button on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Language selector toggle pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PureWhite)
                    .border(1.dp, OutlineLight, RoundedCornerShape(20.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (currentLanguage == AppLanguage.ENGLISH) ForestGreenPrimary else Color.Transparent)
                        .clickable { onLanguageChange(AppLanguage.ENGLISH) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("onboarding_lang_en")
                ) {
                    Text(
                        text = "EN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (currentLanguage == AppLanguage.ENGLISH) PureWhite else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (currentLanguage == AppLanguage.BANGLA) ForestGreenPrimary else Color.Transparent)
                        .clickable { onLanguageChange(AppLanguage.BANGLA) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("onboarding_lang_bn")
                ) {
                    Text(
                        text = "বাংলা",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (currentLanguage == AppLanguage.BANGLA) PureWhite else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Skip Button (PRD 6.2: Skip must work)
            Text(
                text = stringResource(R.string.onboarding_skip),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onCompleteOnboarding)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("onboarding_skip_button")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Animated Content (Visual + Text)
        AnimatedContent(
            targetState = currentSlide,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally(tween(260)) { width -> width / 3 } + fadeIn(tween(260)))
                        .togetherWith(slideOutHorizontally(tween(260)) { width -> -width / 3 } + fadeOut(tween(260)))
                } else {
                    (slideInHorizontally(tween(260)) { width -> -width / 3 } + fadeIn(tween(260)))
                        .togetherWith(slideOutHorizontally(tween(260)) { width -> width / 3 } + fadeOut(tween(260)))
                }
            },
            label = "onboarding_slide_transition",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { slideIndex ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Botanical Art Container matching mockup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    when (slideIndex) {
                        0 -> OnboardingArtDetectPlantProblems(modifier = Modifier.fillMaxSize())
                        1 -> OnboardingArtExpertGuidance(modifier = Modifier.fillMaxSize())
                        2 -> OnboardingArtGreenerTomorrow(modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Slide Title
                Text(
                    text = stringResource(slides[slideIndex].titleRes),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        lineHeight = 30.sp
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .testTag("onboarding_title_$slideIndex")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Slide Description
                Text(
                    text = stringResource(slides[slideIndex].descriptionRes),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .testTag("onboarding_desc_$slideIndex")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Controls: Page Indicator Dots + Primary Action Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                slides.indices.forEach { index ->
                    val isSelected = index == currentSlide
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ForestGreenPrimary else Color(0xFFD4E6DC))
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Button
            val buttonLabel = when (currentSlide) {
                0 -> stringResource(R.string.onboarding_get_started)
                1 -> stringResource(R.string.onboarding_next)
                else -> stringResource(R.string.onboarding_start_exploring)
            }

            AgriCarePrimaryButton(
                text = buttonLabel,
                onClick = {
                    if (currentSlide < slides.size - 1) {
                        currentSlide++
                    } else {
                        onCompleteOnboarding()
                    }
                },
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_primary_button")
            )
        }
    }
}
