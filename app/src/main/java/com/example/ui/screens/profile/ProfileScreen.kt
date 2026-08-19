package com.example.ui.screens.profile

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
import com.example.data.local.entity.RiderDocumentEntity
import com.example.data.local.entity.RiderEntity
import com.example.ui.components.TukTukStatusBadge
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    rider: RiderEntity?,
    documents: List<RiderDocumentEntity>,
    onToggleVerification: (String) -> Unit,
    onEditBankDetails: (holder: String, acc: String, ifsc: String, upi: String) -> Unit,
    onOpenSupport: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditBankDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var holderInput by remember(rider) { mutableStateOf(rider?.bankAccountHolder ?: "Ravi Kumar") }
    var accInput by remember(rider) { mutableStateOf(rider?.bankAccountNumber ?: "501002348912") }
    var ifscInput by remember(rider) { mutableStateOf(rider?.ifscCode ?: "HDFC0001234") }
    var upiInput by remember(rider) { mutableStateOf(rider?.upiId ?: "ravi@okhdfcbank") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Rider ID Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(TukTukGreenLight, CircleShape)
                            .border(2.dp, TukTukGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (rider?.riderCode?.takeLast(2) ?: "RK"),
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = TukTukGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rider?.riderCode ?: "TT-1082",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TukTukTextPrimary
                        )
                        Text(
                            text = "Joined ${rider?.joinedDate ?: "Aug 2026"} • ${rider?.city ?: "Bengaluru"}",
                            fontSize = 12.sp,
                            color = TukTukTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TukTukStatusBadge(status = rider?.verificationStatus ?: "PENDING")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "⭐ ${rider?.rating ?: 4.85}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TukTukAmberWarning
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = TukTukCardBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Vehicle", fontSize = 11.sp, color = TukTukTextSecondary)
                        Text("${rider?.vehicleType ?: "Motorcycle"} (${rider?.vehicleNumber ?: "KA 03 AB 1234"})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TukTukTextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Driving Licence", fontSize = 11.sp, color = TukTukTextSecondary)
                        Text(rider?.drivingLicenseNumber ?: "DL-0420190014298", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TukTukTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Performance Stats
        Text(
            text = "Quality & Performance Ratings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TukTukTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QualityStatChip(
                label = "Acceptance",
                value = "${rider?.acceptanceRate ?: 96}%",
                icon = Icons.Default.ThumbUp,
                tint = TukTukGreenPrimary,
                modifier = Modifier.weight(1f)
            )
            QualityStatChip(
                label = "On-Time",
                value = "${rider?.onTimeRate ?: 98}%",
                icon = Icons.Default.Timer,
                tint = TukTukBlue,
                modifier = Modifier.weight(1f)
            )
            QualityStatChip(
                label = "Completion",
                value = "${((rider?.completedDeliveries ?: 15) * 100) / (rider?.totalDeliveries?.coerceAtLeast(1) ?: 16)}%",
                icon = Icons.Default.CheckCircle,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Document Verification Status Section
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
                    Text(
                        text = "KYC Documents (${documents.size})",
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )

                    // Quick Toggle for Test Demo
                    TextButton(
                        onClick = {
                            val newStatus = if (rider?.verificationStatus == "VERIFIED") "PENDING" else "VERIFIED"
                            onToggleVerification(newStatus)
                        }
                    ) {
                        Text(
                            text = if (rider?.verificationStatus == "VERIFIED") "Set Pending (Demo)" else "Fast-Verify (Demo)",
                            fontWeight = FontWeight.Bold,
                            color = TukTukGreenDark,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                documents.forEach { doc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.docTitle,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = TukTukTextPrimary
                            )
                            Text(
                                text = doc.docIdentifier ?: doc.remarks ?: "Uploaded",
                                fontSize = 11.sp,
                                color = TukTukTextSecondary
                            )
                        }
                        TukTukStatusBadge(status = doc.status)
                    }
                    if (doc != documents.last()) {
                        Divider(color = TukTukCardBorder.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bank Details Card
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
                    Text(
                        text = "Bank & Settlement Account",
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )

                    IconButton(onClick = { showEditBankDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Bank Details", tint = TukTukGreenPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text("A/C Holder: ${rider?.bankAccountHolder ?: "Ravi Kumar"}", fontSize = 13.sp, color = TukTukTextSecondary)
                Text("Account No: **** **** ${rider?.bankAccountNumber?.takeLast(4) ?: "8912"}", fontSize = 13.sp, color = TukTukTextSecondary)
                Text("IFSC Code: ${rider?.ifscCode ?: "HDFC0001234"}", fontSize = 13.sp, color = TukTukTextSecondary)
                Text("Instant UPI: ${rider?.upiId ?: "ravi@okhdfcbank"}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TukTukGreenDark)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Options List
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
        ) {
            Column {
                ProfileOptionRow(
                    icon = Icons.Default.SupportAgent,
                    title = "Help & Support Tickets",
                    subtitle = "Order issues, restaurant delays, payments",
                    onClick = onOpenSupport
                )
                Divider(color = TukTukCardBorder)
                ProfileOptionRow(
                    icon = Icons.Default.Tune,
                    title = "Platform & Gateway Settings",
                    subtitle = "Payout rates, merchant UPI ID & configs",
                    onClick = onOpenSettings
                )
                Divider(color = TukTukCardBorder)
                ProfileOptionRow(
                    icon = Icons.Default.Emergency,
                    title = "Emergency Safety Helpline",
                    subtitle = "24x7 Roadside & SOS Medical Support (112)",
                    iconTint = TukTukAccentRed,
                    onClick = onOpenSupport
                )
                Divider(color = TukTukCardBorder)
                ProfileOptionRow(
                    icon = Icons.Default.Logout,
                    title = "Logout Session",
                    subtitle = "Sign out from delivery partner account",
                    iconTint = TukTukAccentRed,
                    onClick = { showLogoutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Edit Bank Dialog
    if (showEditBankDialog) {
        AlertDialog(
            onDismissRequest = { showEditBankDialog = false },
            title = { Text("Update Bank & UPI Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = holderInput,
                        onValueChange = { holderInput = it },
                        label = { Text("Account Holder") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accInput,
                        onValueChange = { accInput = it },
                        label = { Text("Account Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ifscInput,
                        onValueChange = { ifscInput = it.uppercase() },
                        label = { Text("IFSC Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = upiInput,
                        onValueChange = { upiInput = it },
                        label = { Text("UPI ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditBankDetails(holderInput, accInput, ifscInput, upiInput)
                        showEditBankDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("SAVE CHANGES")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBankDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold, color = TukTukAccentRed) },
            text = { Text("Are you sure you want to log out? Your status will be switched to OFFLINE.", color = TukTukTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukAccentRed)
                ) {
                    Text("LOG OUT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun QualityStatChip(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TukTukTextPrimary)
            Text(text = label, fontSize = 11.sp, color = TukTukTextSecondary)
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = TukTukGreenPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconTint.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TukTukTextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TukTukTextSecondary)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TukTukTextMuted)
    }
}
