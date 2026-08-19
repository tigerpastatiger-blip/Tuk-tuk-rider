package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String,
    val area: String,
    val landmark: String? = null,
    val lat: Double,
    val lng: Double,
    val deliveryInstructions: String = "Please ring bell and leave with security if unreachable."
)
