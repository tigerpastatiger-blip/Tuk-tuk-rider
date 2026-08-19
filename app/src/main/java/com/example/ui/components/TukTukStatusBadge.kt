package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun TukTukStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status.uppercase()) {
        "VERIFIED", "DELIVERED", "PAID", "ONLINE", "RESOLVED" -> Triple(
            TukTukGreenLight,
            TukTukGreenDark,
            TukTukGreenPrimary.copy(alpha = 0.4f)
        )
        "PENDING", "ASSIGNED", "GOING_TO_RESTAURANT", "GOING_TO_CUSTOMER", "OPEN", "IN_PROGRESS", "PROCESSING" -> Triple(
            TukTukAmberContainer,
            TukTukAmberWarning,
            TukTukAmberWarning.copy(alpha = 0.3f)
        )
        "REJECTED", "CANCELLED", "FAILED", "OFFLINE", "CLOSED", "CRITICAL" -> Triple(
            TukTukRedContainer,
            TukTukAccentRed,
            TukTukAccentRed.copy(alpha = 0.3f)
        )
        "PREPAID" -> Triple(
            TukTukBlueContainer,
            TukTukBlue,
            TukTukBlue.copy(alpha = 0.3f)
        )
        "COD" -> Triple(
            Color(0xFFFEF08A),
            Color(0xFF854D0E),
            Color(0xFFEAB308).copy(alpha = 0.4f)
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline
        )
    }

    val displayLabel = when (status.uppercase()) {
        "GOING_TO_RESTAURANT" -> "En Route (Pickup)"
        "ARRIVED_AT_RESTAURANT" -> "At Restaurant"
        "ORDER_PICKED_UP" -> "Picked Up"
        "GOING_TO_CUSTOMER" -> "En Route (Delivery)"
        "ARRIVED_AT_CUSTOMER" -> "At Location"
        "DELIVERED" -> "Delivered ✓"
        "PREPAID" -> "Prepaid (Online)"
        "COD" -> "Cash On Delivery (COD)"
        else -> status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(100.dp))
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayLabel,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
