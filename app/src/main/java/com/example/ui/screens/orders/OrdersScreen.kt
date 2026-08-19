package com.example.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.data.model.OrderDetail
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TukTukStatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    orders: List<OrderEntity>,
    onSelectOrder: (Long) -> Unit,
    onNavigateToActiveDelivery: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedOrderDetail by remember { mutableStateOf<OrderEntity?>(null) }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "COMPLETED" -> orders.filter { it.status == "DELIVERED" }
            "ACTIVE" -> orders.filter { it.status !in listOf("DELIVERED", "CANCELLED", "FAILED") }
            "CANCELLED" -> orders.filter { it.status in listOf("CANCELLED", "FAILED") }
            else -> orders
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
    ) {
        // Top Filter Tabs
        Surface(color = TukTukWhite, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Deliveries",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TukTukTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL" to "All", "ACTIVE" to "Active", "COMPLETED" to "Delivered", "CANCELLED" to "Cancelled").forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TukTukGreenPrimary,
                                selectedLabelColor = TukTukWhite,
                                containerColor = TukTukSurfaceVariant,
                                labelColor = TukTukTextPrimary
                            ),
                            border = null
                        )
                    }
                }
            }
        }

        // Orders List
        if (filteredOrders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.ReceiptLong,
                title = "No Orders Found",
                subtitle = when (selectedFilter) {
                    "ACTIVE" -> "You have no active orders in progress right now."
                    "COMPLETED" -> "No completed deliveries found yet."
                    "CANCELLED" -> "No cancelled deliveries."
                    else -> "No order records available."
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderHistoryCard(
                        order = order,
                        onClick = {
                            if (order.status !in listOf("DELIVERED", "CANCELLED", "FAILED")) {
                                onNavigateToActiveDelivery()
                            } else {
                                selectedOrderDetail = order
                            }
                        }
                    )
                }
            }
        }
    }

    // Detailed Order Bottom Sheet / Dialog
    if (selectedOrderDetail != null) {
        val order = selectedOrderDetail!!
        val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

        ModalBottomSheet(
            onDismissRequest = { selectedOrderDetail = null },
            containerColor = TukTukWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Order #${order.orderNumber}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TukTukTextPrimary
                        )
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = TukTukTextSecondary
                        )
                    }
                    TukTukStatusBadge(status = order.status)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = TukTukCardBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Payout Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TukTukGreenContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rider Payout Summary",
                            fontWeight = FontWeight.Bold,
                            color = TukTukGreenDark,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Base Delivery Fee (${order.distanceKm} km)", color = TukTukTextSecondary, fontSize = 13.sp)
                            Text("₹${order.riderEarning.toInt()}", fontWeight = FontWeight.SemiBold, color = TukTukTextPrimary, fontSize = 13.sp)
                        }

                        if (order.incentive > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Peak Hour Incentive", color = TukTukTextSecondary, fontSize = 13.sp)
                                Text("+₹${order.incentive.toInt()}", fontWeight = FontWeight.SemiBold, color = TukTukGreenDark, fontSize = 13.sp)
                            }
                        }

                        if (order.tip > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Customer Tip", color = TukTukTextSecondary, fontSize = 13.sp)
                                Text("+₹${order.tip.toInt()}", fontWeight = FontWeight.SemiBold, color = TukTukGreenDark, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = TukTukGreenPrimary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Net Earning", fontWeight = FontWeight.Bold, color = TukTukGreenDark, fontSize = 15.sp)
                            Text(
                                "₹${order.riderEarning.toInt() + order.incentive.toInt() + order.tip.toInt()}",
                                fontWeight = FontWeight.Black,
                                color = TukTukGreenPrimary,
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Payment Method", color = TukTukTextSecondary, fontSize = 13.sp)
                    TukTukStatusBadge(status = order.paymentMethod)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Order Bill", color = TukTukTextSecondary, fontSize = 13.sp)
                    Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = TukTukTextPrimary)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { selectedOrderDetail = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("CLOSE DETAILS", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
    val totalEarnings = order.riderEarning.toInt() + order.incentive.toInt() + order.tip.toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TukTukTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TukTukStatusBadge(status = order.status)
                }

                Text(
                    text = "+₹$totalEarnings",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = TukTukGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${order.distanceKm} km • ${order.estMinutes} mins • $dateStr",
                fontSize = 12.sp,
                color = TukTukTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = TukTukCardBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (order.paymentMethod == "COD") "💵 Cash on Delivery (₹${order.totalAmount.toInt()})" else "💳 Prepaid Online",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TukTukTextSecondary
                    )
                }

                Text(
                    text = "View Breakdown →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TukTukGreenDark
                )
            }
        }
    }
}
