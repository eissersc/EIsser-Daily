package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueTurquoise
import com.example.ui.theme.DeepCurrent

@Composable
fun HydrationProgressRing(
    current: Double,
    goal: Double,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 12.dp
) {
    val progress = if (goal > 0) (current / goal).coerceIn(0.0, 1.0).toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "HydrationProgressRing"
    )

    // Pulsing dot animation for the status pill
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("hydration_progress_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Water Drop Watermark Top-Right
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = BlueTurquoise.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section Title: "Hydration Progress"
                Text(
                    text = "HYDRATION PROGRESS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCurrent.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Ring Box
                Box(
                    modifier = Modifier
                        .size(size)
                        .testTag("hydration_progress_ring_container"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(strokeWidth / 2)
                    ) {
                        // Background track
                        drawCircle(
                            color = Color(0xFFE9ECEF),
                            style = Stroke(width = strokeWidth.toPx())
                        )

                        // Progress Arc
                        drawArc(
                            color = BlueTurquoise,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Central info text: e.g. "1.8L" and "of 2.5L Goal"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "%.1f".format(current / 1000.0),
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DeepCurrent,
                                    fontSize = 32.sp
                                )
                            )
                            Text(
                                text = "L",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCurrent.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.padding(bottom = 4.dp, start = 1.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "of %.1fL Goal".format(goal / 1000.0),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepCurrent.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status pill: bg-[#53B0AE]/10 px-4 py-2 rounded-full
                val isHydrated = progress >= 0.75f
                val statusText = if (isHydrated) "Status: Hydrated" else "Status: Keep Hydrating"
                val statusColor = if (isHydrated) BlueTurquoise else DeepCurrent.copy(alpha = 0.7f)

                Surface(
                    color = BlueTurquoise.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pulsing dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .graphicsLayer { alpha = pulseAlpha }
                                .background(color = BlueTurquoise, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
