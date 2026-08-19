package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_status_history")
data class OrderStatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val status: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
