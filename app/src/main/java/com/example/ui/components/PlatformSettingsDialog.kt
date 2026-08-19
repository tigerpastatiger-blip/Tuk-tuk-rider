package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SystemSettingEntity
import com.example.ui.theme.*

@Composable
fun PlatformSettingsDialog(
    settings: List<SystemSettingEntity>,
    onUpdateSetting: (key: String, value: String) -> Unit,
    onDismiss: () -> Unit
) {
    var editingKey by remember { mutableStateOf<String?>(null) }
    var editValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Platform & System Settings", fontWeight = FontWeight.Bold, color = TukTukTextPrimary)
        },
        text = {
            Column {
                Text(
                    text = "Operational configuration for Tuk Tuk dispatch engine, payout parameters and merchant UPI gateways.",
                    fontSize = 12.sp,
                    color = TukTukTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(settings, key = { it.key }) { setting ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = TukTukSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = setting.key.replace("_", " "),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TukTukGreenDark
                                )
                                Text(
                                    text = setting.value,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = TukTukTextPrimary
                                )
                                if (setting.description.isNotBlank()) {
                                    Text(
                                        text = setting.description,
                                        fontSize = 11.sp,
                                        color = TukTukTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
            ) {
                Text("DONE")
            }
        }
    )
}
