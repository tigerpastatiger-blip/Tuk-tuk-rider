package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.RiderEntity
import com.example.data.model.OrderDetail
import com.example.ui.components.TukTukStatusBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    rider: RiderEntity?,
    activeOrder: OrderEntity?,
    activeOrderDetail: OrderDetail?,
    onToggleOnline: (Boolean) -> Unit,
    onRequestNewDelivery: () -> Unit,
    onNavigateToActiveDelivery: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToEarnings: () -> Unit,
    onFastTrackVerify: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOfflineConfirmDialog by remember { mutableStateOf(false) }

    val isOnline = rider?.isOnline == true
    val isVerified = rider?.verificationStatus == "VERIFIED"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(TukTukGreenLight)
                            .border(2.dp, TukTukGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (rider?.riderCode?.takeLast(2) ?: "RK"),
                            fontWeight = FontWeight.Bold,
                            color = TukTukGreenDark,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Namaste, ${rider?.riderCode ?: "Partner"} 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TukTukTextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            TukTukStatusBadge(status = rider?.verificationStatus ?: "PENDING")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⭐ ${rider?.rating ?: 4.85}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TukTukAmberWarning
                            )
                        }
                    }
                }
            }
        }

        // Verification Alert Banner if Not Verified
        if (!isVerified) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TukTukAmberContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PendingActions,
                            contentDescription = null,
                            tint = TukTukAmberWarning,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KYC Verification in Progress",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your documents are currently under review. Once verified, you will be able to switch online and accept deliveries.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78350F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onFastTrackVerify("VERIFIED") },
                        colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Approve KYC for Testing", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Online / Offline Hero Switch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOnline) TukTukGreenContainer else TukTukWhite
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (isOnline) TukTukGreenPrimary else TukTukCardBorder
                ),
                width = if (isOnline) 2.dp else 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isOnline) "YOU ARE ONLINE" else "YOU ARE OFFLINE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isOnline) TukTukGreenDark else TukTukTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isOnline) "🟢 Ready to receive deliveries" else "⚪ Go online to receive order assignments",
                            style = MaterialTheme.typography.bodySmall,
                            color = TukTukTextSecondary
                        )
                    }

                    // Pulse indicator
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (isOnline) TukTukGreenPrimary else Color.LightGray,
                                CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isOnline) {
                            if (activeOrder != null) {
                                showOfflineConfirmDialog = true
                            } else {
                                onToggleOnline(false)
                            }
                        } else {
                            onToggleOnline(true)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("online_offline_toggle_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOnline) TukTukAccentRed else TukTukGreenPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.PowerSettingsNew else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isOnline) "GO OFFLINE" else "GO ONLINE & DELIVER",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Active Delivery Card if one is in progress
        if (activeOrder != null && activeOrder.status != "ASSIGNED") {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToActiveDelivery() }
                    .testTag("active_order_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(TukTukGreenPrimary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVE DELIVERY IN PROGRESS",
                                color = TukTukGreenLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        TukTukStatusBadge(status = activeOrder.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Order #${activeOrder.orderNumber}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (activeOrderDetail != null) {
                        Text(
                            text = "Restaurant: ${activeOrderDetail.restaurant.name}",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Deliver To: ${activeOrderDetail.customer.name} (${activeOrderDetail.customer.area})",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Earnings: ₹${activeOrder.riderEarning.toInt()}",
                            color = TukTukGreenLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Button(
                            onClick = onNavigateToActiveDelivery,
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("VIEW DELIVERY FLOW →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Key Metrics Grid
        Text(
            text = "Today's Performance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TukTukTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeStatCard(
                title = "Earnings",
                value = "₹${(rider?.completedDeliveries ?: 0) * 58 + 80}",
                subtitle = "Net for today",
                icon = Icons.Default.CurrencyRupee,
                iconTint = TukTukGreenPrimary,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToEarnings() }
            )
            HomeStatCard(
                title = "Deliveries",
                value = "${rider?.completedDeliveries ?: 3}",
                subtitle = "Completed",
                icon = Icons.Default.DeliveryDining,
                iconTint = TukTukBlue,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToOrders() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeStatCard(
                title = "Distance",
                value = "${rider?.todayDistanceKm ?: 18.5} km",
                subtitle = "Ridden today",
                icon = Icons.Default.Navigation,
                iconTint = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
            HomeStatCard(
                title = "COD Cash",
                value = "₹${(rider?.codCollectedToday ?: 840.0) - (rider?.codDepositedToday ?: 600.0)}",
                subtitle = "Pending deposit",
                icon = Icons.Default.Payments,
                iconTint = TukTukAmberWarning,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToEarnings() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Quest & Surge Incentives Banner
        val completedCount = rider?.completedDeliveries ?: 3
        val targetDeliveries = 6
        val targetProgress = (completedCount.toFloat() / targetDeliveries).coerceIn(0f, 1f)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)), // Rich emerald forest
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(TukTukGreenLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎯", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY INCENTIVE QUEST",
                                color = TukTukGreenLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Earn ₹150 Bonus on 6 Orders",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF059669), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+₹150",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$completedCount of $targetDeliveries completed",
                        color = TukTukGreenLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(targetDeliveries - completedCount).coerceAtLeast(0)} orders to go",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { targetProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TukTukGreenLight,
                    trackColor = Color(0xFF047857),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instant Order Simulation Trigger Card (for Testing the Core Lifecycle MVP)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = TukTukGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live Order Dispatch",
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Simulate an incoming order from a nearby restaurant to test the full 30s countdown, acceptance, pickup, OTP delivery, and earnings ledger update.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TukTukTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onRequestNewDelivery,
                    enabled = isOnline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("simulate_order_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenDark)
                ) {
                    Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnline) "SIMULATE INCOMING DELIVERY" else "GO ONLINE FIRST TO RECEIVE ORDERS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Confirmation Dialog for going offline during active delivery
    if (showOfflineConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showOfflineConfirmDialog = false },
            title = {
                Text(
                    text = "Active Delivery in Progress",
                    fontWeight = FontWeight.Bold,
                    color = TukTukAccentRed
                )
            },
            text = {
                Text(
                    text = "You currently have an active order #${activeOrder?.orderNumber}. Are you sure you want to go offline? Please complete the delivery before ending your shift.",
                    color = TukTukTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleOnline(false)
                        showOfflineConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukAccentRed)
                ) {
                    Text("GO OFFLINE ANYWAY")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfflineConfirmDialog = false }) {
                    Text("KEEP ONLINE")
                }
            }
        )
    }
}

@Composable
private fun HomeStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TukTukTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconTint.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = TukTukTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TukTukTextMuted
            )
        }
    }
}
