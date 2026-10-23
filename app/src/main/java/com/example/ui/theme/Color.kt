package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Palette per User Specification
val PrimaryPurple = Color(0xFF7C3AED)
val PrimaryPurpleDark = Color(0xFF5B21B6)
val PrimaryPurpleLight = Color(0xFFDDD6FE)
val AccentPink = Color(0xFFEC4899)
val AccentPinkLight = Color(0xFFFCE7F3)

val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceSubtle = Color(0xFFF4F4F6)

val BackgroundDark = Color(0xFF0F0B1E)
val SurfaceDark = Color(0xFF1B1530)
val SurfaceSubtleDark = Color(0xFF261E42)

// Gold Success Accent
val SuccessGold = Color(0xFFF59E0B)
val SuccessGoldLight = Color(0xFFFEF3C7)
val SuccessGoldDark = Color(0xFFB45309)

// Text Colors
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF4B5563)
val TextMuted = Color(0xFF9CA3AF)

val TextPrimaryDark = Color(0xFFF9FAFB)
val TextSecondaryDark = Color(0xFFD1D5DB)
val TextMutedDark = Color(0xFF9CA3AF)

// Progress Bar Gradient
val PurplePinkGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryPurple, AccentPink)
)

val GoldCelebrationGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFBBF24), SuccessGold, Color(0xFFD97706))
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(PrimaryPurple.copy(alpha = 0.08f), AccentPink.copy(alpha = 0.04f))
)

// Category Accent Colors
val CategoryFitness = Color(0xFF10B981)
val CategoryCareer = Color(0xFF6366F1)
val CategoryLearning = Color(0xFFF59E0B)
val CategoryHealth = Color(0xFFF43F5E)
val CategoryFinance = Color(0xFF06B6D4)
val CategoryCreative = Color(0xFFD946EF)
val CategoryPersonal = Color(0xFF8B5CF6)
