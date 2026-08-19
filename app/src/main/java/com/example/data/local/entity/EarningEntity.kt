package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "earnings")
data class EarningEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val riderId: Long,
    val orderId: Long?,
    val orderNumber: String?,
    val dateString: String, // YYYY-MM-DD
    val baseEarning: Double,
    val incentive: Double = 0.0,
    val tip: Double = 0.0,
    val bonus: Double = 0.0,
    val codCollected: Double = 0.0,
    val totalNet: Double,
    val timestamp: Long = System.currentTimeMillis()
)
