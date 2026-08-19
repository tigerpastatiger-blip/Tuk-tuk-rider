package com.example.ui.screens.activeorder

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.data.model.OrderDetail
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LiveDeliveryMapCanvas
import com.example.ui.components.TukTukStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveOrderScreen(
    orderDetail: OrderDetail?,
    onArrivedAtRestaurant: () -> Unit,
    onConfirmPickup: () -> Unit,
    onArrivedAtCustomer: () -> Unit,
    onCompleteDelivery: (enteredOtp: String, isCodReceived: Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (orderDetail == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(TukTukBackground)
        ) {
            TopAppBar(
                title = { Text("Active Delivery", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TukTukWhite)
            )
            EmptyStateView(
                icon = Icons.Default.Moped,
                title = "No Active Delivery",
                subtitle = "You do not have any delivery in progress. Switch online on the Home screen to receive orders.",
                actionButtonText = "GO TO HOME",
                onActionClick = onNavigateBack
            )
        }
        return
    }

    val order = orderDetail.order
    val restaurant = orderDetail.restaurant
    val customer = orderDetail.customer
    val items = orderDetail.items

    var enteredOtp by remember(order.id) { mutableStateOf(order.customerOtp) } // Pre-fill with correct OTP for ease of testing
    var isCodConfirmed by remember(order.id) { mutableStateOf(order.paymentMethod != "COD") }
    var showCodConfirmDialog by remember { mutableStateOf(false) }

    // Stages:
    // 1: GOING_TO_RESTAURANT
    // 2: ARRIVED_AT_RESTAURANT
    // 3: GOING_TO_CUSTOMER
    // 4: ARRIVED_AT_CUSTOMER
    val currentStage = when (order.status) {
        "GOING_TO_RESTAURANT", "ACCEPTED" -> 1
        "ARRIVED_AT_RESTAURANT" -> 2
        "GOING_TO_CUSTOMER", "ORDER_PICKED_UP" -> 3
        "ARRIVED_AT_CUSTOMER" -> 4
        else -> 1
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Driving-focused Minimal Header
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TukTukTextPrimary
                    )
                    Text(
                        text = "Earning: ₹${order.riderEarning.toInt() + order.incentive.toInt() + order.tip.toInt()} • ${order.distanceKm} km",
                        fontSize = 12.sp,
                        color = TukTukGreenDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TukTukTextPrimary)
                }
            },
            actions = {
                TukTukStatusBadge(status = order.paymentMethod, modifier = Modifier.padding(end = 12.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = TukTukWhite)
        )

        // Progress Timeline Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TukTukWhite)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val stages = listOf("To Restaurant", "Pickup", "To Customer", "Deliver")
            stages.forEachIndexed { index, title ->
                val stageNum = index + 1
                val isDone = stageNum < currentStage
                val isCurrent = stageNum == currentStage

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                when {
                                    isDone -> TukTukGreenPrimary
                                    isCurrent -> TukTukGreenLight
                                    else -> TukTukSurfaceVariant
                                },
                                CircleShape
                            )
                            .border(2.dp, if (isCurrent) TukTukGreenPrimary else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = TukTukWhite, modifier = Modifier.size(16.dp))
                        } else {
                            Text(
                                text = "$stageNum",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) TukTukGreenDark else TukTukTextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) TukTukGreenDark else TukTukTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Live Route Map Navigation Canvas
            LiveDeliveryMapCanvas(
                currentStage = currentStage,
                restaurantName = restaurant.name,
                customerName = customer.name,
                distanceKm = order.distanceKm
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stage 1 & 2: Restaurant Pickup Cards
            if (currentStage <= 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = TukTukGreenPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RESTAURANT PICKUP",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TukTukGreenDark
                                )
                            }
                            Text(
                                text = "OTP: ${order.restaurantOtp}",
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TukTukTextPrimary
                        )

                        Text(
                            text = restaurant.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TukTukTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Open Map & Call Restaurant
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode("${restaurant.name}, ${restaurant.address}")}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(Intent.createChooser(mapIntent, "Open Navigation"))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TukTukBlue)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("OPEN MAP", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${restaurant.phone}"))
                                    context.startActivity(dialIntent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CALL")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Items Checklist Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Order Items (${items.size})",
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary
                            )
                            Text(
                                text = "Verify bag before pickup",
                                fontSize = 11.sp,
                                color = TukTukGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (item.isVeg) "🟢" else "🔴",
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${item.quantity}x ${item.itemName}",
                                        fontWeight = FontWeight.Medium,
                                        color = TukTukTextPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = "₹${item.price.toInt()}",
                                    color = TukTukTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Stage 3 & 4: Customer Delivery Cards
            if (currentStage >= 3) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TukTukAccentRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CUSTOMER DROP OFF",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TukTukAccentRed
                                )
                            }
                            TukTukStatusBadge(status = order.paymentMethod)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TukTukTextPrimary
                        )

                        Text(
                            text = customer.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TukTukTextSecondary
                        )

                        if (!customer.landmark.isNullOrBlank()) {
                            Text(
                                text = "Landmark: ${customer.landmark}",
                                fontSize = 12.sp,
                                color = TukTukTextMuted
                            )
                        }

                        if (customer.deliveryInstructions.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = TukTukAmberWarning, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = customer.deliveryInstructions,
                                        fontSize = 12.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Navigate, Call & Quick Message
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(customer.address)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(Intent.createChooser(mapIntent, "Open Navigation"))
                                },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TukTukBlue)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("MAP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                    context.startActivity(dialIntent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CALL", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${customer.phone}")).apply {
                                        putExtra("sms_body", "Hi ${customer.name}, I am your Tuk Tuk delivery partner. I'm en route with your order #${order.orderNumber}!")
                                    }
                                    context.startActivity(smsIntent)
                                },
                                modifier = Modifier.weight(1.1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = TukTukGreenDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("MSG", fontSize = 12.sp, color = TukTukGreenDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // If Stage 4 (At Customer): COD Payment & OTP Verification
                if (currentStage == 4) {
                    Spacer(modifier = Modifier.height(14.dp))

                    if (order.paymentMethod == "COD") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCodConfirmed) TukTukGreenContainer else Color(0xFFFEF2F2)
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isCodConfirmed) TukTukGreenPrimary else TukTukAccentRed
                                ),
                                width = 2.dp
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "CASH ON DELIVERY",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCodConfirmed) TukTukGreenDark else TukTukAccentRed,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "COLLECT ₹${order.totalAmount.toInt()}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 24.sp,
                                            color = if (isCodConfirmed) TukTukGreenDark else TukTukAccentRed
                                        )
                                    }

                                    Button(
                                        onClick = { showCodConfirmDialog = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCodConfirmed) TukTukGreenPrimary else TukTukAccentRed
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isCodConfirmed) Icons.Default.CheckCircle else Icons.Default.Payments,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isCodConfirmed) "CASH RECEIVED ✓" else "CONFIRM CASH",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    } else {
                        // Prepaid notification
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = TukTukBlueContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = TukTukBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ORDER ALREADY PAID ONLINE",
                                        fontWeight = FontWeight.Bold,
                                        color = TukTukBlue,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Do not collect any cash from the customer.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Customer OTP Input Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Enter 4-digit Delivery OTP",
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary
                            )
                            Text(
                                text = "Ask the customer for the delivery verification code",
                                fontSize = 12.sp,
                                color = TukTukTextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) enteredOtp = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("delivery_otp_input"),
                                placeholder = { Text("4-digit OTP (Hint: ${order.customerOtp})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = TukTukGreenPrimary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TukTukGreenPrimary,
                                    unfocusedBorderColor = TukTukCardBorder
                                )
                            )
                        }
                    }
                }
            }
        }

        // Prominent Single Bottom Action Button (Driving-safe)
        Surface(
            color = TukTukWhite,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (currentStage) {
                    1 -> {
                        Button(
                            onClick = onArrivedAtRestaurant,
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("arrived_at_restaurant_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ARRIVED AT RESTAURANT", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    2 -> {
                        Button(
                            onClick = onConfirmPickup,
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("confirm_pickup_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenDark)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ORDER PICKED UP & START DELIVERY", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                    3 -> {
                        Button(
                            onClick = onArrivedAtCustomer,
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("arrived_at_customer_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ARRIVED AT CUSTOMER", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    4 -> {
                        Button(
                            onClick = { onCompleteDelivery(enteredOtp, isCodConfirmed) },
                            enabled = !isLoading && (order.paymentMethod != "COD" || isCodConfirmed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("complete_delivery_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = TukTukWhite, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("COMPLETE DELIVERY", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // COD Confirmation Dialog
    if (showCodConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCodConfirmDialog = false },
            title = {
                Text(
                    text = "Confirm Cash Collection",
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Have you collected the full amount of ₹${order.totalAmount.toInt()} in cash from ${customer.name}?",
                        color = TukTukTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "This amount will be added to your COD Pending balance to be deposited later.",
                        fontSize = 12.sp,
                        color = TukTukTextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isCodConfirmed = true
                        showCodConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("YES, CASH RECEIVED")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCodConfirmDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
