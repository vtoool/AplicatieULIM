package com.victor.ulim.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Stable
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.victor.ulim.ui.AppViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Replays its content with a fade + slide entrance, delayed by [index] steps.
 * Only animates the first time it becomes visible (state is saved across navigation).
 */
@Composable
fun Stagger(
    index: Int,
    modifier: Modifier = Modifier,
    baseDelayMs: Long = 60,
    stepMs: Long = 80,
    content: @Composable () -> Unit
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!visible) {
            delay(baseDelayMs + index * stepMs)
            visible = true
        }
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(500, easing = FastOutSlowInEasing)) +
            slideInVertically(tween(550, easing = FastOutSlowInEasing)) { it / 3 }
    ) {
        content()
    }
}

/** Horizontal shake used for wrong passwords and incomplete forms. */
@Stable
class ShakeState {
    private val animatable = Animatable(0f)

    val value: Float get() = animatable.value

    suspend fun shake() {
        animatable.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 520
                0f at 0 using LinearEasing
                -14f at 70
                14f at 140
                -10f at 210
                10f at 280
                -6f at 360
                6f at 440
                0f at 520
            }
        )
    }
}

@Composable
fun rememberShakeState(): ShakeState = remember { ShakeState() }

fun Modifier.shake(state: ShakeState): Modifier = graphicsLayer { translationX = state.value }

/** Slowly rotating multi-color gradient with soft drifting orbs. */
@Composable
fun AnimatedGradientBackground(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition(label = "ulim-bg")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(18_000, easing = LinearEasing)),
        label = "angle"
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(11_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )

    val dark = isSystemInDarkTheme()
    val gradientColors = listOf(
        cs.primaryContainer.copy(alpha = if (dark) 0.9f else 0.55f),
        cs.tertiaryContainer.copy(alpha = if (dark) 0.7f else 0.65f),
        cs.secondaryContainer.copy(alpha = if (dark) 0.75f else 0.5f)
    ) + cs.surface

    Box(
        modifier.fillMaxSize().drawBehind {
            val radius = maxOf(size.width, size.height)
            val radians = Math.toRadians(angle.toDouble())
            val dx = cos(radians).toFloat() * radius
            val dy = sin(radians).toFloat() * radius
            drawRect(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(center.x - dx, center.y - dy),
                    end = Offset(center.x + dx, center.y + dy)
                )
            )
            drawCircle(
                cs.tertiary.copy(alpha = 0.20f),
                radius = size.width * (0.30f + breathe * 0.06f),
                center = center + Offset(size.width * 0.32f, -size.height * 0.18f + drift * 40f)
            )
            drawCircle(
                cs.primary.copy(alpha = 0.15f),
                radius = size.width * 0.38f,
                center = center + Offset(-size.width * 0.30f, size.height * 0.25f - drift * 30f)
            )
            drawCircle(
                cs.secondary.copy(alpha = 0.14f),
                radius = size.width * 0.26f,
                center = center + Offset(size.width * 0.18f, size.height * 0.36f + breathe * 30f)
            )
        }
    )
}

/** Circular gradient badge used as the app logo. */
@Composable
fun LogoBadge(size: androidx.compose.ui.unit.Dp = 92.dp, text: String = "U") {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(size)
            .shadow(14.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(cs.primary, cs.tertiary, cs.secondary))
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

/** In-app language switcher: ro (default) / ru / en. */
@Composable
fun LanguageMenu(
    current: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (current) {
        AppViewModel.LANG_RO -> "Română"
        AppViewModel.LANG_RU -> "Русский"
        else -> "English"
    }

    // The Box is the popup's anchor, keeping its menu directly beneath the control.
    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(50),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val languages = listOf(
                AppViewModel.LANG_RO to "Română",
                AppViewModel.LANG_RU to "Русский",
                AppViewModel.LANG_EN to "English"
            )
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            fontWeight = if (code == current) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        if (code == current) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.height(18.dp))
                        } else {
                            Spacer(Modifier.width(18.dp))
                        }
                    },
                    onClick = {
                        onSelect(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

/** Small error caption shown under invalid form sections. */
@Composable
fun FieldErrorText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
}

/** Uppercase-style section label used across the form. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(top = 14.dp, bottom = 4.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
