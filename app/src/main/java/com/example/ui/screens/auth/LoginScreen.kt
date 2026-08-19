package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var phone by remember { mutableStateOf("9876543210") }
    var password by remember { mutableStateOf("password123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotPhone by remember { mutableStateOf("") }

    val isPhoneValid = phone.trim().length == 10 && phone.all { it.isDigit() }
    val isFormValid = isPhoneValid && password.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Brand Hero
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(TukTukGreenLight, CircleShape)
                .border(2.dp, TukTukGreenPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ElectricMoped,
                contentDescription = null,
                tint = TukTukGreenPrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TUK TUK",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = TukTukGreenPrimary,
            letterSpacing = 2.sp
        )

        Text(
            text = "Delivery Partner App",
            style = MaterialTheme.typography.titleSmall,
            color = TukTukTextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = TukTukWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Partner Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
                Text(
                    text = "Enter your registered 10-digit mobile number",
                    style = MaterialTheme.typography.bodySmall,
                    color = TukTukTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Phone Input with +91 Prefix
                Text(
                    text = "Mobile Phone Number",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TukTukTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all { ch -> ch.isDigit() }) phone = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input"),
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                        ) {
                            Text(
                                text = "🇮🇳 +91",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TukTukTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            VerticalDivider(modifier = Modifier.height(20.dp), color = TukTukCardBorder)
                        }
                    },
                    placeholder = { Text("10 digit number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TukTukGreenPrimary,
                        unfocusedBorderColor = TukTukCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TukTukTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TukTukTextSecondary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    placeholder = { Text("Enter password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TukTukGreenPrimary,
                        unfocusedBorderColor = TukTukCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me & Forgot Password Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = TukTukGreenPrimary)
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodySmall,
                            color = TukTukTextSecondary
                        )
                    }

                    TextButton(
                        onClick = {
                            forgotPhone = phone
                            showForgotDialog = true
                        }
                    ) {
                        Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TukTukGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = { onLogin(phone, password) },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("login_submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TukTukGreenPrimary,
                        disabledContainerColor = TukTukGreenPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = TukTukWhite, modifier = Modifier.size(22.dp))
                    } else {
                        Text(
                            text = "LOGIN AS RIDER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Prompt
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TukTukGreenLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Want to earn with Tuk Tuk?",
                    fontWeight = FontWeight.Bold,
                    color = TukTukGreenDark,
                    fontSize = 14.sp
                )
                Text(
                    text = "Flexible hours, instant payouts & accident coverage",
                    style = MaterialTheme.typography.bodySmall,
                    color = TukTukTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("navigate_register_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TukTukGreenDark,
                        containerColor = TukTukWhite
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(TukTukGreenPrimary)
                    )
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REGISTER AS NEW DELIVERY PARTNER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo Fast-login hint
        Text(
            text = "Demo Pre-filled: 9876543210 / password123",
            style = MaterialTheme.typography.labelSmall,
            color = TukTukTextMuted
        )
    }

    // Forgot Password Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Text(
                    text = "Reset Password",
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered mobile number. We will send a secure OTP to reset your password.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TukTukTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotPhone,
                        onValueChange = { if (it.length <= 10 && it.all { ch -> ch.isDigit() }) forgotPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Text("🇮🇳 +91", fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp)) },
                        placeholder = { Text("10 digit number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotPhone.isNotBlank()) {
                            onForgotPassword(forgotPhone)
                            showForgotDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    Text("SEND RESET LINK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
