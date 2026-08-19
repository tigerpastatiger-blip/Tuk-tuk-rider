package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderDetail
import com.example.ui.theme.*

@Composable
fun IncomingOrderSheet(
    orderDetail: OrderDetail,
    countdownSec: Int,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = countdownSec / 30f,
        label = "countdown"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("incoming_order_sheet"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(TukTukGreenPrimary))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with Timer & Earnings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(TukTukGreenLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricMoped,
                            contentDescription = null,
                            tint = TukTukGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "NEW DELIVERY REQUEST",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TukTukGreenDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Order #${orderDetail.order.orderNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TukTukTextSecondary
                        )
                    }
                }

                // Earnings Chip
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${orderDetail.order.riderEarning.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = TukTukGreenPrimary
                    )
                    Text(
                        text = "Estimated Pay",
                        style = MaterialTheme.typography.labelSmall,
                        color = TukTukTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Countdown Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Expires in",
                        fontSize = 11.sp,
                        color = TukTukTextSecondary
                    )
                    Text(
                        text = "${countdownSec}s",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (countdownSec <= 10) TukTukAccentRed else TukTukAmberWarning
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (countdownSec <= 10) TukTukAccentRed else TukTukGreenPrimary,
                    trackColor = TukTukGreenLight,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pickup & Delivery Locations Card
            Card(
                colors = CardDefaults.cardColors(containerColor = TukTukSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Pickup
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = TukTukGreenPrimary,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PICKUP",
                                style = MaterialTheme.typography.labelSmall,
                                color = TukTukTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = orderDetail.restaurant.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary
                            )
                            Text(
                                text = orderDetail.restaurant.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = TukTukTextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .padding(start = 28.dp),
                        color = TukTukCardBorder
                    )

                    // Delivery
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TukTukAccentRed,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DROP OFF",
                                style = MaterialTheme.typography.labelSmall,
                                color = TukTukTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${orderDetail.customer.name} (${orderDetail.customer.area})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary
                            )
                            Text(
                                text = orderDetail.customer.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = TukTukTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip(
                    icon = Icons.Default.NearMe,
                    label = "${orderDetail.order.distanceKm} km total"
                )
                MetricChip(
                    icon = Icons.Default.Schedule,
                    label = "~${orderDetail.order.estMinutes} mins"
                )
                MetricChip(
                    icon = Icons.Default.Payment,
                    label = if (orderDetail.order.paymentMethod == "COD") "COD ₹${orderDetail.order.totalAmount.toInt()}" else "PREPAID"
                )
            }

            if (orderDetail.order.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TukTukAmberWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = orderDetail.order.specialInstructions,
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Accept / Decline Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("decline_order_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TukTukAccentRed
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(TukTukAccentRed.copy(alpha = 0.5f))
                    )
                ) {
                    Text("DECLINE", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp)
                        .testTag("accept_order_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TukTukGreenPrimary
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ACCEPT ORDER", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    icon: ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(TukTukSurfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TukTukTextSecondary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TukTukTextPrimary
        )
    }
}
