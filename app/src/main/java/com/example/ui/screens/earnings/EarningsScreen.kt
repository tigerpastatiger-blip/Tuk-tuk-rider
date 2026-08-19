package com.example.ui.screens.earnings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.EarningEntity
import com.example.data.local.entity.PayoutEntity
import com.example.data.local.entity.RiderEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TukTukStatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    rider: RiderEntity?,
    earnings: List<EarningEntity>,
    payouts: List<PayoutEntity>,
    onRequestPayout: (Double, String) -> Unit,
    onRecordCodDeposit: (Double) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("EARNINGS") } // EARNINGS, PAYOUTS, COD
    var showPayoutDialog by remember { mutableStateOf(false) }
    var showCodDepositDialog by remember { mutableStateOf(false) }

    var payoutAmountInput by remember { mutableStateOf("500") }
    var codDepositInput by remember { mutableStateOf("300") }

    val totalNetEarnings = remember(earnings) {
        earnings.sumOf { it.totalNet }
    }
    val totalBase = remember(earnings) { earnings.sumOf { it.baseEarning } }
    val totalIncentive = remember(earnings) { earnings.sumOf { it.incentive } }
    val totalTips = remember(earnings) { earnings.sumOf { it.tip } }

    val codPending = (rider?.codCollectedToday ?: 840.0) - (rider?.codDepositedToday ?: 600.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
    ) {
        // Hero Header
        Surface(color = TukTukWhite, shadowElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Earnings & Settlements",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = TukTukTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("EARNINGS" to "Earnings Summary", "PAYOUTS" to "Bank Payouts", "COD" to "COD Cash Ledger").forEach { (key, title) ->
                        val isSelected = selectedSection == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSection = key },
                            label = { Text(title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedSection) {
                "EARNINGS" -> {
                    // Total Earnings Hero Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = TukTukGreenDark),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "TOTAL NET EARNINGS",
                                    color = TukTukGreenLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "₹${totalNetEarnings.toInt()}",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = Color.White.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Breakdown Rows
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Base Pay", color = TukTukGreenLight, fontSize = 11.sp)
                                        Text("₹${totalBase.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Column {
                                        Text("Surge / Bonus", color = TukTukGreenLight, fontSize = 11.sp)
                                        Text("₹${totalIncentive.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Column {
                                        Text("Tips", color = TukTukGreenLight, fontSize = 11.sp)
                                        Text("₹${totalTips.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order Earnings History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary
                            )
                            Text(
                                text = "${earnings.size} records",
                                fontSize = 12.sp,
                                color = TukTukTextSecondary
                            )
                        }
                    }

                    if (earnings.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.CurrencyRupee,
                                title = "No Earnings Yet",
                                subtitle = "Complete your first delivery order to start earning payout credits."
                            )
                        }
                    } else {
                        items(earnings, key = { it.id }) { item ->
                            EarningItemCard(earning = item)
                        }
                    }
                }

                "PAYOUTS" -> {
                    // Payout Account & Action Hero
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Available Payout Balance",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TukTukTextSecondary
                                        )
                                        Text(
                                            text = "₹${totalNetEarnings.toInt()}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Black,
                                            color = TukTukGreenPrimary
                                        )
                                    }

                                    Button(
                                        onClick = { showPayoutDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("request_payout_button")
                                    ) {
                                        Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WITHDRAW", fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = TukTukCardBorder)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TukTukBlue, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "UPI: ${rider?.upiId ?: "ravi@okhdfcbank"} • Bank: ****${rider?.bankAccountNumber?.takeLast(4) ?: "8102"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TukTukTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Payout History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TukTukTextPrimary
                        )
                    }

                    if (payouts.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.Receipt,
                                title = "No Payouts Requested",
                                subtitle = "Your bank withdrawal history and settlement statements will appear here."
                            )
                        }
                    } else {
                        items(payouts, key = { it.id }) { payout ->
                            PayoutHistoryCard(payout = payout)
                        }
                    }
                }

                "COD" -> {
                    // COD Ledger Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (codPending > 0) Color(0xFFFEF3C7) else TukTukGreenLight
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (codPending > 0) TukTukAmberWarning else TukTukGreenPrimary
                                )
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "CASH ON DELIVERY (COD) SUMMARY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (codPending > 0) Color(0xFF92400E) else TukTukGreenDark
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "₹${codPending.toInt()} Pending",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (codPending > 0) Color(0xFF92400E) else TukTukGreenDark
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = Color.Black.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Cash Collected", fontSize = 11.sp, color = TukTukTextSecondary)
                                        Text("₹${rider?.codCollectedToday?.toInt() ?: 840}", fontWeight = FontWeight.Bold, color = TukTukTextPrimary)
                                    }
                                    Column {
                                        Text("Total Deposited", fontSize = 11.sp, color = TukTukTextSecondary)
                                        Text("₹${rider?.codDepositedToday?.toInt() ?: 600}", fontWeight = FontWeight.Bold, color = TukTukGreenDark)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { showCodDepositDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenDark),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RECORD COD DEPOSIT", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "COD Deposit Policy",
                                    fontWeight = FontWeight.Bold,
                                    color = TukTukTextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Maximum allowed pending COD limit is ₹2,000.\n• Deposit cash at any authorized Tuk Tuk hub or UPI partner point before reaching the threshold to continue receiving delivery requests.\n• All deposits reflect immediately in your account.",
                                    fontSize = 12.sp,
                                    color = TukTukTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Payout Request Dialog
    if (showPayoutDialog) {
        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            title = {
                Text(
                    text = "Withdraw to Bank / UPI",
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter the amount you wish to withdraw to your linked account (${rider?.upiId ?: "ravi@okhdfcbank"}).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TukTukTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = payoutAmountInput,
                        onValueChange = { payoutAmountInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = payoutAmountInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onRequestPayout(amt, rider?.upiId ?: "ravi@okhdfcbank")
                            showPayoutDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("CONFIRM WITHDRAWAL")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // COD Deposit Dialog
    if (showCodDepositDialog) {
        AlertDialog(
            onDismissRequest = { showCodDepositDialog = false },
            title = {
                Text(
                    text = "Record Cash Deposit",
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter the amount of cash deposited at the collection point or via merchant QR code.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TukTukTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = codDepositInput,
                        onValueChange = { codDepositInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = codDepositInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onRecordCodDeposit(amt)
                            showCodDepositDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("SUBMIT DEPOSIT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCodDepositDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun EarningItemCard(earning: EarningEntity) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(earning.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Order #${earning.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TukTukTextPrimary
                )
                Text(
                    text = "$dateStr • Base ₹${earning.baseEarning.toInt()}${if (earning.incentive > 0) " + Surge ₹${earning.incentive.toInt()}" else ""}${if (earning.tip > 0) " + Tip ₹${earning.tip.toInt()}" else ""}",
                    fontSize = 12.sp,
                    color = TukTukTextSecondary
                )
            }

            Text(
                text = "+₹${earning.totalNet.toInt()}",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = TukTukGreenPrimary
            )
        }
    }
}

@Composable
private fun PayoutHistoryCard(payout: PayoutEntity) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(payout.requestedAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Withdrawal #${payout.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TukTukTextPrimary
                    )
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = TukTukTextSecondary
                    )
                }

                TukTukStatusBadge(status = payout.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${payout.bankReference}",
                    fontSize = 12.sp,
                    color = TukTukTextSecondary
                )
                Text(
                    text = "₹${payout.amount.toInt()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TukTukTextPrimary
                )
            }
        }
    }
}
