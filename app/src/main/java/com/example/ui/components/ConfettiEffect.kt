package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessGold
import kotlin.random.Random

private data class Particle(
    val startX: Float,
    val speedX: Float,
    val speedY: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun ConfettiCelebration(
    isActive: Boolean,
    onFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }
    val colors = listOf(
        SuccessGold,
        PrimaryPurple,
        AccentPink,
        Color(0xFF38BDF8),
        Color(0xFF34D399),
        Color(0xFFF43F5E)
    )

    val particles = remember {
        List(60) {
            Particle(
                startX = Random.nextFloat(),
                speedX = (Random.nextFloat() - 0.5f) * 160f,
                speedY = 600f + Random.nextFloat() * 500f,
                size = 12f + Random.nextFloat() * 16f,
                color = colors[Random.nextInt(colors.size)],
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val alpha = if (p > 0.7f) (1f - p) / 0.3f else 1f

        particles.forEach { particle ->
            val x = (particle.startX * size.width) + (particle.speedX * p)
            val y = (particle.speedY * p) - 50f

            if (y in -50f..size.height + 50f && x in -50f..size.width + 50f) {
                drawRect(
                    color = particle.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    topLeft = Offset(x, y),
                    size = Size(particle.size, particle.size * 0.6f)
                )
            }
        }
    }
}
