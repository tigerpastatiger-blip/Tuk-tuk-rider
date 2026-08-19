package com.example.ui.screens.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SupportTicketEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TukTukStatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    tickets: List<SupportTicketEntity>,
    onCreateTicket: (category: String, subject: String, description: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCreateTicketDialog by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("ORDER_ISSUE") }
    var subjectInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

    val categories = listOf(
        "ORDER_ISSUE" to "Active Order Issue",
        "RESTAURANT_DELAY" to "Restaurant Delay / Closed",
        "CUSTOMER_UNREACHABLE" to "Customer Unreachable / Wrong Address",
        "PAYMENT_COD" to "COD / Payout Discrepancy",
        "ACCIDENT_EMERGENCY" to "Emergency / Vehicle Breakdown"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
    ) {
        TopAppBar(
            title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = TukTukWhite)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Emergency SOS Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukAccentRed))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(TukTukAccentRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "24x7 Safety & SOS Helpline",
                                    fontWeight = FontWeight.Bold,
                                    color = TukTukAccentRed,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "For road accidents or personal emergency assistance",
                                    fontSize = 11.sp,
                                    color = TukTukTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                                context.startActivity(dialIntent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukAccentRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CALL EMERGENCY HELPLINE (112)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Raise Ticket Action
            item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Have an issue with a delivery?",
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Raise a ticket with our partner operations team",
                                fontSize = 12.sp,
                                color = TukTukTextSecondary
                            )
                        }

                        Button(
                            onClick = { showCreateTicketDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("raise_ticket_button")
                        ) {
                            Text("NEW TICKET", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tickets List Header
            item {
                Text(
                    text = "My Support Tickets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
            }

            if (tickets.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.ConfirmationNumber,
                        title = "No Support Tickets",
                        subtitle = "You have not submitted any support tickets."
                    )
                }
            } else {
                items(tickets, key = { it.id }) { ticket ->
                    SupportTicketCard(ticket = ticket)
                }
            }
        }
    }

    // Create Support Ticket Dialog
    if (showCreateTicketDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTicketDialog = false },
            title = { Text("Create Support Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TukTukTextSecondary)

                    categories.forEach { (catKey, catLabel) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = catKey }
                        ) {
                            RadioButton(
                                selected = selectedCategory == catKey,
                                onClick = { selectedCategory = catKey },
                                colors = RadioButtonDefaults.colors(selectedColor = TukTukGreenPrimary)
                            )
                            Text(catLabel, fontSize = 13.sp, color = TukTukTextPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        label = { Text("Subject") },
                        placeholder = { Text("e.g. Restaurant item unavailable") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("Details / Description") },
                        placeholder = { Text("Explain the issue in detail...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subjectInput.isNotBlank()) {
                            onCreateTicket(selectedCategory, subjectInput, descriptionInput)
                            showCreateTicketDialog = false
                            subjectInput = ""
                            descriptionInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("SUBMIT TICKET")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTicketDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun SupportTicketCard(ticket: SupportTicketEntity) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(ticket.createdAt))

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
                Text(
                    text = ticket.ticketNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TukTukTextPrimary
                )
                TukTukStatusBadge(status = ticket.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ticket.subject,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TukTukTextPrimary
            )

            if (ticket.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ticket.description,
                    fontSize = 12.sp,
                    color = TukTukTextSecondary,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = TukTukCardBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Category: ${ticket.category.replace("_", " ")}",
                    fontSize = 11.sp,
                    color = TukTukTextSecondary
                )
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TukTukTextMuted
                )
            }
        }
    }
}
