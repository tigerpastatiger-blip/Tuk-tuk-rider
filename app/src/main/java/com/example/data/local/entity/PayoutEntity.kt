package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val riderId: Long,
    val amount: Double,
    val status: String = "PAID", // PAID, PROCESSING, PENDING
    val bankReference: String = "NEFT-${(100000..999999).random()}",
    val destinationAccount: String = "HDFC Bank (..4920)",
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = System.currentTimeMillis()
)
