package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val area: String,
    val phone: String,
    val cuisine: String,
    val lat: Double,
    val lng: Double,
    val rating: Double = 4.5
)
