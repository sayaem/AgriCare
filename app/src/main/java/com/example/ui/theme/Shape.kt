package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AgriCareShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),  // Buttons & medium chips
    large = RoundedCornerShape(20.dp),   // Cards & dialogs
    extraLarge = RoundedCornerShape(28.dp) // Bottom sheets / search bar
)
