package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerId: Long,
    val restaurantId: Long,
    val riderId: Long?,
    val status: String, // ASSIGNED, ACCEPTED, GOING_TO_RESTAURANT, ARRIVED_AT_RESTAURANT, ORDER_PICKED_UP, GOING_TO_CUSTOMER, ARRIVED_AT_CUSTOMER, DELIVERED, CANCELLED, FAILED
    val totalAmount: Double,
    val deliveryFee: Double = 35.0,
    val riderEarning: Double = 52.0,
    val incentive: Double = 0.0,
    val tip: Double = 0.0,
    val paymentMethod: String, // PREPAID, COD
    val paymentStatus: String, // PAID, PENDING, FAILED
    val customerOtp: String = "4829",
    val restaurantOtp: String = "1923",
    val specialInstructions: String = "Handle with care, hot soup inside. Please ring bell twice.",
    val distanceKm: Double = 3.2,
    val estMinutes: Int = 18,
    val createdAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val pickedUpAt: Long? = null,
    val deliveredAt: Long? = null,
    val cancellationReason: String? = null
)
