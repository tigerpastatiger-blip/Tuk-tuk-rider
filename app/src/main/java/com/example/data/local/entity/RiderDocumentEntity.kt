package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rider_documents")
data class RiderDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val riderId: Long,
    val docType: String, // PROFILE_PHOTO, DRIVING_LICENSE, VEHICLE_RC, ID_PROOF, BANK_PASSBOOK
    val docTitle: String,
    val docIdentifier: String,
    val status: String = "VERIFIED", // PENDING, VERIFIED, REJECTED
    val remarks: String = "Document verified by operations",
    val uploadedAt: Long = System.currentTimeMillis()
)
