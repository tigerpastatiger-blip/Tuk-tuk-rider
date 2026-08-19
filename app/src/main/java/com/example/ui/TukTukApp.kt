package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.screens.activeorder.ActiveOrderScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterMultiStepScreen
import com.example.ui.screens.earnings.EarningsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.orders.OrdersScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.support.SupportScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.TukTukViewModel

enum class SubScreen {
    MAIN_TABS,
    ACTIVE_DELIVERY,
    SUPPORT
}

enum class AuthSubScreen {
    LOGIN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TukTukApp(
    viewModel: TukTukViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authUiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val rider by viewModel.currentRider.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    val activeOrderDetail by viewModel.activeOrderDetail.collectAsState()
    val incomingOrderDetail by viewModel.incomingOrderDetail.collectAsState()
    val incomingCountdown by viewModel.incomingCountdownSec.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val earningsList by viewModel.earningsList.collectAsState()
    val payoutsList by viewModel.payoutsList.collectAsState()
    val documentsList by viewModel.riderDocuments.collectAsState()
    val notificationsList by viewModel.notificationsList.collectAsState()
    val supportTicketsList by viewModel.supportTicketsList.collectAsState()
    val systemSettings by viewModel.systemSettings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentSubScreen by remember { mutableStateOf(SubScreen.MAIN_TABS) }
    var authSubScreen by remember { mutableStateOf(AuthSubScreen.LOGIN) }

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Show snackbars automatically
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Auto-navigate to active delivery screen when order is accepted
    LaunchedEffect(activeOrder?.status) {
        if (activeOrder != null && activeOrder?.status != "ASSIGNED" && activeOrder?.status != "DELIVERED") {
            // Keep on current or let user open
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (authState) {
            is AuthUiState.LoggedOut, is AuthUiState.Idle -> {
                when (authSubScreen) {
                    AuthSubScreen.LOGIN -> {
                        LoginScreen(
                            onLogin = { phone, password -> viewModel.login(phone, password) },
                            onNavigateToRegister = { authSubScreen = AuthSubScreen.REGISTER },
                            onForgotPassword = { phone -> viewModel.resetPassword(phone) },
                            isLoading = isLoading
                        )
                    }
                    AuthSubScreen.REGISTER -> {
                        RegisterMultiStepScreen(
                            onRegisterSubmit = { name, phone, email, pass, dob, addr, city, pin, vType, vNum, dlNum, bHolder, bAcc, ifsc, upi ->
                                viewModel.register(name, phone, email, pass, dob, addr, city, pin, vType, vNum, dlNum, bHolder, bAcc, ifsc, upi)
                            },
                            onNavigateBackToLogin = { authSubScreen = AuthSubScreen.LOGIN },
                            isLoading = isLoading
                        )
                    }
                }
            }

            is AuthUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TukTukBackground),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TukTukGreenPrimary)
                }
            }

            is AuthUiState.LoggedIn, is AuthUiState.Error -> {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (currentSubScreen == SubScreen.MAIN_TABS) {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(TukTukGreenLight, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ElectricMoped,
                                                contentDescription = null,
                                                tint = TukTukGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "TUK TUK",
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.sp,
                                                fontSize = 17.sp,
                                                color = TukTukGreenPrimary
                                            )
                                            Text(
                                                text = if (rider?.isOnline == true) "🟢 Online • ${rider?.currentArea ?: "Indiranagar"}" else "⚪ Offline",
                                                fontSize = 11.sp,
                                                color = TukTukTextSecondary
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { showNotificationsDialog = true }) {
                                        BadgedBox(
                                            badge = {
                                                val unread = notificationsList.count { !it.isRead }
                                                if (unread > 0) {
                                                    Badge(containerColor = TukTukAccentRed) { Text("$unread") }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = "Notifications",
                                                tint = TukTukTextPrimary
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = TukTukWhite)
                            )
                        }
                    },
                    bottomBar = {
                        if (currentSubScreen == SubScreen.MAIN_TABS) {
                            NavigationBar(
                                containerColor = TukTukWhite,
                                tonalElevation = 8.dp
                            ) {
                                AppNavTab.values().forEach { tab ->
                                    val isSelected = selectedTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.selectTab(tab) },
                                        icon = {
                                            Icon(
                                                imageVector = when (tab) {
                                                    AppNavTab.HOME -> Icons.Default.Home
                                                    AppNavTab.ORDERS -> Icons.Default.DeliveryDining
                                                    AppNavTab.EARNINGS -> Icons.Default.CurrencyRupee
                                                    AppNavTab.PROFILE -> Icons.Default.Person
                                                },
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.label,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = TukTukGreenPrimary,
                                            selectedTextColor = TukTukGreenPrimary,
                                            indicatorColor = TukTukGreenLight,
                                            unselectedIconColor = TukTukTextSecondary,
                                            unselectedTextColor = TukTukTextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        when (currentSubScreen) {
                            SubScreen.MAIN_TABS -> {
                                when (selectedTab) {
                                    AppNavTab.HOME -> HomeScreen(
                                        rider = rider,
                                        activeOrder = activeOrder,
                                        activeOrderDetail = activeOrderDetail,
                                        onToggleOnline = { viewModel.toggleOnlineStatus(it) },
                                        onRequestNewDelivery = { viewModel.requestNewDelivery() },
                                        onNavigateToActiveDelivery = { currentSubScreen = SubScreen.ACTIVE_DELIVERY },
                                        onNavigateToOrders = { viewModel.selectTab(AppNavTab.ORDERS) },
                                        onNavigateToEarnings = { viewModel.selectTab(AppNavTab.EARNINGS) },
                                        onFastTrackVerify = { viewModel.setVerificationStatus(it) }
                                    )
                                    AppNavTab.ORDERS -> OrdersScreen(
                                        orders = allOrders,
                                        onSelectOrder = { /* Details in sheet */ },
                                        onNavigateToActiveDelivery = { currentSubScreen = SubScreen.ACTIVE_DELIVERY }
                                    )
                                    AppNavTab.EARNINGS -> EarningsScreen(
                                        rider = rider,
                                        earnings = earningsList,
                                        payouts = payoutsList,
                                        onRequestPayout = { amt, dest -> viewModel.requestPayout(amt, dest) },
                                        onRecordCodDeposit = { amt -> viewModel.recordCodDeposit(amt) },
                                        isLoading = isLoading
                                    )
                                    AppNavTab.PROFILE -> ProfileScreen(
                                        rider = rider,
                                        documents = documentsList,
                                        onToggleVerification = { viewModel.setVerificationStatus(it) },
                                        onEditBankDetails = { h, a, i, u -> viewModel.updateBankDetails(h, a, i, u) },
                                        onOpenSupport = { currentSubScreen = SubScreen.SUPPORT },
                                        onOpenSettings = { showSettingsDialog = true },
                                        onLogout = { viewModel.logout() }
                                    )
                                }
                            }
                            SubScreen.ACTIVE_DELIVERY -> {
                                ActiveOrderScreen(
                                    orderDetail = activeOrderDetail,
                                    onArrivedAtRestaurant = { viewModel.markArrivedAtRestaurant() },
                                    onConfirmPickup = { viewModel.confirmOrderPickup() },
                                    onArrivedAtCustomer = { viewModel.markArrivedAtCustomer() },
                                    onCompleteDelivery = { otp, cod ->
                                        viewModel.completeDelivery(otp, cod)
                                        currentSubScreen = SubScreen.MAIN_TABS
                                    },
                                    onNavigateBack = { currentSubScreen = SubScreen.MAIN_TABS },
                                    isLoading = isLoading
                                )
                            }
                            SubScreen.SUPPORT -> {
                                SupportScreen(
                                    tickets = supportTicketsList,
                                    onCreateTicket = { cat, sub, desc -> viewModel.createSupportTicket(cat, sub, desc) },
                                    onNavigateBack = { currentSubScreen = SubScreen.MAIN_TABS }
                                )
                            }
                        }

                        // Floating Incoming Order Alert Sheet
                        AnimatedVisibility(
                            visible = incomingOrderDetail != null,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            incomingOrderDetail?.let { orderDetail ->
                                IncomingOrderSheet(
                                    orderDetail = orderDetail,
                                    countdownSec = incomingCountdown,
                                    onAccept = {
                                        viewModel.acceptIncomingOrder()
                                        currentSubScreen = SubScreen.ACTIVE_DELIVERY
                                    },
                                    onDecline = { viewModel.declineIncomingOrder() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showNotificationsDialog) {
            NotificationsDialog(
                notifications = notificationsList,
                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                onDismiss = { showNotificationsDialog = false }
            )
        }

        if (showSettingsDialog) {
            PlatformSettingsDialog(
                settings = systemSettings,
                onUpdateSetting = { k, v -> viewModel.updateSystemSetting(k, v) },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}
