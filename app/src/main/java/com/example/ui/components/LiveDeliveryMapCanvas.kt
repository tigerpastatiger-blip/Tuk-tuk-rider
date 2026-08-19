package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LiveDeliveryMapCanvas(
    currentStage: Int,
    restaurantName: String,
    customerName: String,
    distanceKm: Double,
    modifier: Modifier = Modifier
) {
    // Pulse animation for rider position
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Animated progression along route based on stage
    val targetFraction = when (currentStage) {
        1 -> 0.35f
        2 -> 0.50f
        3 -> 0.75f
        4 -> 0.95f
        else -> 0.25f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(1000, easing = EaseInOutCubic),
        label = "riderProgress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Night dark navigation mode
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF334155)))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // 1. Draw stylized city road network grid
                val roadColor = Color(0xFF1E293B)
                val highwayColor = Color(0xFF334155)

                // Secondary streets
                for (x in 40..width.toInt() step 60) {
                    drawLine(roadColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), height), strokeWidth = 3f)
                }
                for (y in 30..height.toInt() step 50) {
                    drawLine(roadColor, Offset(0f, y.toFloat()), Offset(width, y.toFloat()), strokeWidth = 3f)
                }

                // Diagonal arterial expressway
                drawLine(highwayColor, Offset(0f, height * 0.8f), Offset(width, height * 0.2f), strokeWidth = 6f)

                // 2. Define route waypoints: Start (Rider Hub) -> Restaurant -> Customer
                val pStart = Offset(width * 0.15f, height * 0.70f)
                val pRest = Offset(width * 0.45f, height * 0.35f)
                val pCust = Offset(width * 0.85f, height * 0.65f)

                val routePath = Path().apply {
                    moveTo(pStart.x, pStart.y)
                    cubicTo(
                        width * 0.25f, height * 0.45f,
                        width * 0.35f, height * 0.30f,
                        pRest.x, pRest.y
                    )
                    cubicTo(
                        width * 0.55f, height * 0.40f,
                        width * 0.70f, height * 0.80f,
                        pCust.x, pCust.y
                    )
                }

                // Draw route background line
                drawPath(
                    path = routePath,
                    color = Color(0xFF475569),
                    style = Stroke(width = 8f)
                )

                // Draw active highlighted route segment
                drawPath(
                    path = routePath,
                    color = TukTukGreenPrimary,
                    style = Stroke(
                        width = 8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                    )
                )

                // 3. Draw Restaurant Waypoint
                drawCircle(Color(0xFF22C55E), radius = 10f, center = pRest)
                drawCircle(Color.White, radius = 5f, center = pRest)

                // 4. Draw Customer Destination Waypoint
                drawCircle(TukTukAccentRed, radius = 12f, center = pCust)
                drawCircle(Color.White, radius = 6f, center = pCust)

                // 5. Calculate interpolated rider position along path
                val currentRiderPos = when {
                    animatedProgress <= 0.5f -> {
                        val t = animatedProgress / 0.5f
                        Offset(
                            pStart.x + (pRest.x - pStart.x) * t,
                            pStart.y + (pRest.y - pStart.y) * t
                        )
                    }
                    else -> {
                        val t = (animatedProgress - 0.5f) / 0.5f
                        Offset(
                            pRest.x + (pCust.x - pRest.x) * t,
                            pRest.y + (pCust.y - pRest.y) * t
                        )
                    }
                }

                // Draw Rider Pulse
                drawCircle(
                    color = TukTukGreenPrimary.copy(alpha = pulseAlpha),
                    radius = pulseRadius,
                    center = currentRiderPos
                )
                // Draw Rider Core Dot
                drawCircle(
                    color = TukTukGreenPrimary,
                    radius = 8f,
                    center = currentRiderPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = currentRiderPos
                )
            }

            // Waypoint Label Overlay (Restaurant & Customer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Route Navigation Chip
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(TukTukGreenPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentStage) {
                                1 -> "Heading to $restaurantName"
                                2 -> "At $restaurantName (Pickup)"
                                3 -> "Delivering to $customerName"
                                4 -> "At $customerName (Drop-off)"
                                else -> "Live GPS Navigation"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Speed & Distance Remaining Chip
                Box(
                    modifier = Modifier
                        .background(TukTukGreenDark, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${(distanceKm * (1f - animatedProgress)).coerceAtLeast(0.2).let { String.format("%.1f", it) }} km left",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
