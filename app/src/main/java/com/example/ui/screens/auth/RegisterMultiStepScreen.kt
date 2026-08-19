package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMultiStepScreen(
    onRegisterSubmit: (
        fullName: String,
        phone: String,
        email: String?,
        password: String,
        dob: String,
        address: String,
        city: String,
        pinCode: String,
        vehicleType: String,
        vehicleNumber: String,
        drivingLicenseNumber: String,
        bankHolder: String,
        bankAccount: String,
        ifscCode: String,
        upiId: String
    ) -> Unit,
    onNavigateBackToLogin: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // Step 1: Account
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Step 2: Personal & Location
    var dob by remember { mutableStateOf("1999-08-20") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Bengaluru") }
    var pinCode by remember { mutableStateOf("") }

    // Step 3: Vehicle & DL
    var vehicleType by remember { mutableStateOf("Motorcycle") }
    var vehicleNumber by remember { mutableStateOf("") }
    var drivingLicenseNumber by remember { mutableStateOf("") }

    // Step 4: Documents & Bank Details
    var bankHolder by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var docDlUploaded by remember { mutableStateOf(true) }
    var docRcUploaded by remember { mutableStateOf(true) }
    var docIdUploaded by remember { mutableStateOf(true) }

    val vehicleOptions = listOf(
        "Motorcycle" to Icons.Default.TwoWheeler,
        "Scooter" to Icons.Default.Moped,
        "Bicycle" to Icons.Default.PedalBike,
        "Electric vehicle" to Icons.Default.ElectricMoped,
        "Other" to Icons.Default.DirectionsCar
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TukTukBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Rider Onboarding",
                    fontWeight = FontWeight.Bold,
                    color = TukTukTextPrimary
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onNavigateBackToLogin()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TukTukTextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = TukTukWhite)
        )

        // Progress Stepper
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TukTukWhite)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (step in 1..4) {
                val isCompleted = step < currentStep
                val isCurrent = step == currentStep

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when {
                                isCompleted -> TukTukGreenPrimary
                                isCurrent -> TukTukGreenLight
                                else -> TukTukSurfaceVariant
                            },
                            CircleShape
                        )
                        .border(
                            2.dp,
                            if (isCurrent) TukTukGreenPrimary else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = TukTukWhite, modifier = Modifier.size(18.dp))
                    } else {
                        Text(
                            text = "$step",
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) TukTukGreenDark else TukTukTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                if (step < 4) {
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        color = if (step < currentStep) TukTukGreenPrimary else TukTukCardBorder,
                        thickness = 2.dp
                    )
                }
            }
        }

        // Body Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when (currentStep) {
                1 -> {
                    // Step 1: Basic & Auth
                    Text(
                        text = "Step 1 of 4: Account Credentials",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )
                    Text(
                        text = "Create your Tuk Tuk delivery partner login",
                        style = MaterialTheme.typography.bodySmall,
                        color = TukTukTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedInputField(
                        label = "Full Name (as per Aadhaar / PAN)",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "e.g. Ravi Kumar"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Mobile Phone Number",
                        value = phone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                        placeholder = "10-digit mobile",
                        prefix = "🇮🇳 +91 ",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Email Address (Optional)",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "ravi@example.com",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Create Password",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "At least 6 characters",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Confirm Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Re-enter password",
                        isPassword = true
                    )
                }

                2 -> {
                    // Step 2: Personal details & Address
                    Text(
                        text = "Step 2 of 4: Personal & Address",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )
                    Text(
                        text = "Provide your address for zone assignment",
                        style = MaterialTheme.typography.bodySmall,
                        color = TukTukTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedInputField(
                        label = "Date of Birth (YYYY-MM-DD)",
                        value = dob,
                        onValueChange = { dob = it },
                        placeholder = "1999-05-15"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Current Residential Address",
                        value = address,
                        onValueChange = { address = it },
                        placeholder = "House/Flat No, Street, Landmark"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedInputField(
                                label = "City",
                                value = city,
                                onValueChange = { city = it },
                                placeholder = "e.g. Bengaluru"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedInputField(
                                label = "PIN Code",
                                value = pinCode,
                                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pinCode = it },
                                placeholder = "6 digits",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }
                }

                3 -> {
                    // Step 3: Vehicle selection
                    Text(
                        text = "Step 3 of 4: Vehicle & License",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )
                    Text(
                        text = "Choose the vehicle you will use for deliveries",
                        style = MaterialTheme.typography.bodySmall,
                        color = TukTukTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Select Vehicle Type",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TukTukTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    vehicleOptions.forEach { (type, icon) ->
                        val isSelected = vehicleType == type
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { vehicleType = type },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) TukTukGreenLight else TukTukWhite
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) TukTukGreenPrimary else TukTukCardBorder
                                ),
                                width = if (isSelected) 2.dp else 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) TukTukGreenDark else TukTukTextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = type,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TukTukGreenDark else TukTukTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { vehicleType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = TukTukGreenPrimary)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Vehicle Registration Number",
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it.uppercase() },
                        placeholder = "e.g. KA 03 HM 4892"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedInputField(
                        label = "Driving Licence Number",
                        value = drivingLicenseNumber,
                        onValueChange = { drivingLicenseNumber = it.uppercase() },
                        placeholder = "e.g. DL-0420190014298"
                    )
                }

                4 -> {
                    // Step 4: Documents & Payout Bank Details
                    Text(
                        text = "Step 4 of 4: Documents & Banking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )
                    Text(
                        text = "Provide bank account & confirm document readiness",
                        style = MaterialTheme.typography.bodySmall,
                        color = TukTukTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Document Checklist Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TukTukWhite),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TukTukCardBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Verification Checklist",
                                fontWeight = FontWeight.Bold,
                                color = TukTukTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            DocUploadRow(
                                title = "Driving Licence Card",
                                isUploaded = docDlUploaded,
                                onToggle = { docDlUploaded = !docDlUploaded }
                            )
                            DocUploadRow(
                                title = "Vehicle RC Certificate",
                                isUploaded = docRcUploaded,
                                onToggle = { docRcUploaded = !docRcUploaded }
                            )
                            DocUploadRow(
                                title = "Aadhaar / Government ID",
                                isUploaded = docIdUploaded,
                                onToggle = { docIdUploaded = !docIdUploaded }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bank & Settlement Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TukTukTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedInputField(
                        label = "Account Holder Name",
                        value = bankHolder,
                        onValueChange = { bankHolder = it },
                        placeholder = "As on bank passbook"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedInputField(
                        label = "Bank Account Number",
                        value = bankAccount,
                        onValueChange = { bankAccount = it },
                        placeholder = "e.g. 501002348912",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedInputField(
                        label = "IFSC Code",
                        value = ifscCode,
                        onValueChange = { ifscCode = it.uppercase() },
                        placeholder = "e.g. HDFC0001234"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedInputField(
                        label = "UPI ID for Instant Payouts",
                        value = upiId,
                        onValueChange = { upiId = it },
                        placeholder = "e.g. name@upi / name@okhdfcbank"
                    )
                }
            }
        }

        // Bottom Action Bar
        Surface(
            color = TukTukWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("PREVIOUS")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < 4) {
                            currentStep++
                        } else {
                            onRegisterSubmit(
                                fullName.ifBlank { "Ravi Partner" },
                                phone.ifBlank { "9876543211" },
                                email.ifBlank { null },
                                password.ifBlank { "password123" },
                                dob,
                                address.ifBlank { "12th Cross, Indiranagar" },
                                city,
                                pinCode.ifBlank { "560038" },
                                vehicleType,
                                vehicleNumber.ifBlank { "KA 03 AB 1234" },
                                drivingLicenseNumber.ifBlank { "DL-0420210098231" },
                                bankHolder.ifBlank { fullName.ifBlank { "Ravi Partner" } },
                                bankAccount.ifBlank { "501002938102" },
                                ifscCode.ifBlank { "HDFC0001234" },
                                upiId.ifBlank { "ravi@okhdfcbank" }
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(50.dp)
                        .testTag("register_next_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TukTukGreenPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = TukTukWhite, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = if (currentStep == 4) "SUBMIT APPLICATION" else "CONTINUE TO STEP ${currentStep + 1}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OutlinedInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    prefix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TukTukTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            prefix = if (prefix != null) { { Text(prefix, fontWeight = FontWeight.Bold, fontSize = 13.sp) } } else null,
            placeholder = { Text(placeholder, fontSize = 13.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TukTukGreenPrimary,
                unfocusedBorderColor = TukTukCardBorder
            )
        )
    }
}

@Composable
private fun DocUploadRow(
    title: String,
    isUploaded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(if (isUploaded) TukTukGreenLight.copy(alpha = 0.5f) else TukTukSurfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                contentDescription = null,
                tint = if (isUploaded) TukTukGreenDark else TukTukTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TukTukTextPrimary
            )
        }

        TextButton(onClick = onToggle) {
            Text(
                text = if (isUploaded) "ATTACHED" else "UPLOAD",
                color = if (isUploaded) TukTukGreenDark else TukTukGreenPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
