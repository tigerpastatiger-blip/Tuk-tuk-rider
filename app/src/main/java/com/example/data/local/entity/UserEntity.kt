package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phone: String,
    val fullName: String,
    val email: String? = null,
    val passwordHash: String,
    val role: String = "RIDER", // RIDER, CUSTOMER, REST_ADMIN, ADMIN
    val createdAt: Long = System.currentTimeMillis()
)
