package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ticketNumber: String = "TK-${(1000..9999).random()}",
    val riderId: Long,
    val orderId: Long? = null,
    val category: String, // ORDER_PROBLEM, RESTAURANT_PROBLEM, CUSTOMER_PROBLEM, PAYMENT_PROBLEM, COD_PROBLEM, APP_PROBLEM, ACCOUNT_PROBLEM, EMERGENCY, OTHER
    val subject: String,
    val description: String,
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val priority: String = "NORMAL", // NORMAL, HIGH, CRITICAL
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolutionNote: String? = null
)
