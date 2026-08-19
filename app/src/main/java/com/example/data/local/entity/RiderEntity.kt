package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riders")
data class RiderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val riderCode: String = "TT-${(1000..9999).random()}",
    val dob: String = "1998-05-14",
    val address: String = "12th Main, HAL 2nd Stage, Indiranagar",
    val city: String = "Bengaluru",
    val pinCode: String = "560038",
    val vehicleType: String = "Motorcycle", // Motorcycle, Scooter, Bicycle, Electric vehicle, Other
    val vehicleNumber: String = "KA 03 HM 4892",
    val drivingLicenseNumber: String = "DL-0420190014298",
    val bankAccountHolder: String = "",
    val bankAccountNumber: String = "",
    val ifscCode: String = "",
    val upiId: String = "",
    val verificationStatus: String = "VERIFIED", // PENDING, VERIFIED, REJECTED
    val isOnline: Boolean = false,
    val currentArea: String = "Indiranagar, Bengaluru",
    val currentLat: Double = 12.9716,
    val currentLng: Double = 77.5946,
    val rating: Double = 4.85,
    val totalDeliveries: Int = 142,
    val completedDeliveries: Int = 138,
    val cancelledDeliveries: Int = 4,
    val acceptanceRate: Int = 96,
    val onTimeRate: Int = 98,
    val totalEarnings: Double = 18450.0,
    val todayDistanceKm: Double = 18.5,
    val codCollectedToday: Double = 840.0,
    val codDepositedToday: Double = 600.0,
    val joinedDate: String = "Nov 2025"
)
