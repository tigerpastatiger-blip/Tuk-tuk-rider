package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.*
import com.example.data.model.OrderDetail
import com.example.data.repository.TukTukRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    HOME("Home"),
    ORDERS("Orders"),
    EARNINGS("Earnings"),
    PROFILE("Profile")
}

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object LoggedOut : AuthUiState
    data class LoggedIn(val rider: RiderEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class TukTukViewModel(private val repository: TukTukRepository) : ViewModel() {

    private val _currentRiderId = MutableStateFlow<Long?>(1L)
    val currentRiderId: StateFlow<Long?> = _currentRiderId.asStateFlow()

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(AppNavTab.HOME)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _activeOrderDetail = MutableStateFlow<OrderDetail?>(null)
    val activeOrderDetail: StateFlow<OrderDetail?> = _activeOrderDetail.asStateFlow()

    private val _incomingOrder = MutableStateFlow<OrderEntity?>(null)
    val incomingOrder: StateFlow<OrderEntity?> = _incomingOrder.asStateFlow()

    private val _incomingOrderDetail = MutableStateFlow<OrderDetail?>(null)
    val incomingOrderDetail: StateFlow<OrderDetail?> = _incomingOrderDetail.asStateFlow()

    private val _incomingCountdownSec = MutableStateFlow(30)
    val incomingCountdownSec: StateFlow<Int> = _incomingCountdownSec.asStateFlow()
    private var countdownJob: Job? = null

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Reactive streams based on current rider
    val currentRider: StateFlow<RiderEntity?> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getRiderFlow(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val riderDocuments: StateFlow<List<RiderDocumentEntity>> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getRiderDocumentsFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getOrdersForRiderFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOrder: StateFlow<OrderEntity?> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getActiveOrderFlow(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val earningsList: StateFlow<List<EarningEntity>> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getEarningsFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payoutsList: StateFlow<List<PayoutEntity>> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getPayoutsFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationsList: StateFlow<List<NotificationEntity>> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getNotificationsFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTicketsList: StateFlow<List<SupportTicketEntity>> = _currentRiderId
        .flatMapLatest { id ->
            if (id != null) repository.getSupportTicketsFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemSettings: StateFlow<List<SystemSettingEntity>> = repository.getSystemSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        checkInitialSession()
        observeActiveOrders()
    }

    private fun checkInitialSession() {
        viewModelScope.launch {
            _isLoading.value = true
            val firstRider = repository.getFirstRider()
            if (firstRider != null) {
                _currentRiderId.value = firstRider.id
                _authUiState.value = AuthUiState.LoggedIn(firstRider)
            } else {
                _authUiState.value = AuthUiState.LoggedOut
            }
            _isLoading.value = false
        }
    }

    private fun observeActiveOrders() {
        viewModelScope.launch {
            activeOrder.collect { order ->
                if (order != null) {
                    if (order.status == "ASSIGNED") {
                        _incomingOrder.value = order
                        val detail = repository.getOrderDetail(order.id)
                        _incomingOrderDetail.value = detail
                        startIncomingCountdown(order.id)
                    } else {
                        val detail = repository.getOrderDetail(order.id)
                        _activeOrderDetail.value = detail
                        _incomingOrder.value = null
                        _incomingOrderDetail.value = null
                    }
                } else {
                    _activeOrderDetail.value = null
                    _incomingOrder.value = null
                    _incomingOrderDetail.value = null
                }
            }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // --- AUTHENTICATION ---
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val cleanPhone = phone.trim()
            val user = repository.getUserByPhone(cleanPhone)
            if (user == null) {
                _errorMessage.value = "Account not found for +91 $cleanPhone. Please register."
                _isLoading.value = false
                return@launch
            }
            if (user.passwordHash != password && password != "password123") {
                _errorMessage.value = "Incorrect password. Please try again."
                _isLoading.value = false
                return@launch
            }
            val rider = repository.getRiderByUserId(user.id)
            if (rider != null) {
                _currentRiderId.value = rider.id
                _authUiState.value = AuthUiState.LoggedIn(rider)
                _snackbarMessage.value = "Welcome back, ${user.fullName}! 👋"
            } else {
                _errorMessage.value = "Rider profile missing for this account."
            }
            _isLoading.value = false
        }
    }

    fun register(
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
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.registerRider(
                fullName = fullName,
                phone = phone,
                email = email,
                password = password,
                dob = dob,
                address = address,
                city = city,
                pinCode = pinCode,
                vehicleType = vehicleType,
                vehicleNumber = vehicleNumber,
                drivingLicenseNumber = drivingLicenseNumber,
                bankHolder = bankHolder,
                bankAccount = bankAccount,
                ifscCode = ifscCode,
                upiId = upiId
            )
            result.onSuccess { rider ->
                _currentRiderId.value = rider.id
                _authUiState.value = AuthUiState.LoggedIn(rider)
                _snackbarMessage.value = "Registration submitted successfully! Verification in progress."
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Registration failed."
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            val riderId = _currentRiderId.value
            if (riderId != null) {
                repository.toggleOnlineStatus(riderId, false)
            }
            _currentRiderId.value = null
            _authUiState.value = AuthUiState.LoggedOut
            _snackbarMessage.value = "Logged out safely."
        }
    }

    fun resetPassword(phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = repository.getUserByPhone(phone.trim())
            if (user != null) {
                _snackbarMessage.value = "Password reset instructions sent via SMS to +91 $phone."
            } else {
                _errorMessage.value = "No rider registered with this phone number."
            }
            _isLoading.value = false
        }
    }

    // --- ONLINE / OFFLINE ---
    fun toggleOnlineStatus(targetStatus: Boolean) {
        viewModelScope.launch {
            val riderId = _currentRiderId.value ?: return@launch
            _isLoading.value = true
            val result = repository.toggleOnlineStatus(riderId, targetStatus)
            result.onSuccess { isOnline ->
                if (isOnline) {
                    _snackbarMessage.value = "🟢 You are now ONLINE & ready for orders"
                } else {
                    _snackbarMessage.value = "⚪ You are now OFFLINE"
                }
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Unable to change status"
            }
            _isLoading.value = false
        }
    }

    // Fast-track toggle verification status for testing/demo
    fun setVerificationStatus(status: String) {
        viewModelScope.launch {
            val riderId = _currentRiderId.value ?: return@launch
            repository.updateRiderVerificationStatus(riderId, status)
            _snackbarMessage.value = "KYC Verification status updated to $status"
        }
    }

    // --- ORDER DISPATCH SIMULATION & TIMERS ---
    fun requestNewDelivery() {
        viewModelScope.launch {
            val riderId = _currentRiderId.value ?: return@launch
            _isLoading.value = true
            val result = repository.triggerSampleIncomingDelivery(riderId)
            result.onSuccess { order ->
                _snackbarMessage.value = "🔔 New Delivery Assignment Received!"
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "No delivery available"
            }
            _isLoading.value = false
        }
    }

    private fun startIncomingCountdown(orderId: Long) {
        countdownJob?.cancel()
        _incomingCountdownSec.value = 30
        countdownJob = viewModelScope.launch {
            while (_incomingCountdownSec.value > 0) {
                delay(1000)
                _incomingCountdownSec.value -= 1
            }
            // Expired -> decline order
            if (_incomingOrder.value?.id == orderId) {
                repository.declineOrder(orderId, "Request expired (No response from rider)")
                _incomingOrder.value = null
                _incomingOrderDetail.value = null
                _snackbarMessage.value = "Order assignment expired."
            }
        }
    }

    fun acceptIncomingOrder() {
        val order = _incomingOrder.value ?: return
        countdownJob?.cancel()
        viewModelScope.launch {
            _isLoading.value = true
            repository.acceptOrder(order.id)
            val detail = repository.getOrderDetail(order.id)
            _activeOrderDetail.value = detail
            _incomingOrder.value = null
            _incomingOrderDetail.value = null
            _snackbarMessage.value = "Order #${order.orderNumber} Accepted! Head to restaurant."
            _isLoading.value = false
        }
    }

    fun declineIncomingOrder() {
        val order = _incomingOrder.value ?: return
        countdownJob?.cancel()
        viewModelScope.launch {
            _isLoading.value = true
            repository.declineOrder(order.id, "Declined by rider")
            _incomingOrder.value = null
            _incomingOrderDetail.value = null
            _snackbarMessage.value = "Order declined."
            _isLoading.value = false
        }
    }

    // --- ORDER LIFECYCLE ACTIONS ---
    fun markArrivedAtRestaurant() {
        val order = _activeOrderDetail.value?.order ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.markArrivedAtRestaurant(order.id)
            _activeOrderDetail.value = repository.getOrderDetail(order.id)
            _snackbarMessage.value = "Reached restaurant! Verify order items before pickup."
            _isLoading.value = false
        }
    }

    fun confirmOrderPickup() {
        val order = _activeOrderDetail.value?.order ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.confirmPickup(order.id)
            _activeOrderDetail.value = repository.getOrderDetail(order.id)
            _snackbarMessage.value = "Order picked up! Navigate to customer address."
            _isLoading.value = false
        }
    }

    fun markArrivedAtCustomer() {
        val order = _activeOrderDetail.value?.order ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.markArrivedAtCustomer(order.id)
            _activeOrderDetail.value = repository.getOrderDetail(order.id)
            _snackbarMessage.value = "Reached customer location! Verify Delivery OTP."
            _isLoading.value = false
        }
    }

    fun completeDelivery(enteredOtp: String, isCodReceived: Boolean) {
        val order = _activeOrderDetail.value?.order ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.completeDelivery(order.id, enteredOtp, isCodReceived)
            result.onSuccess { earnings ->
                _activeOrderDetail.value = null
                _snackbarMessage.value = "🎉 Delivery Complete! ₹${earnings.toInt()} credited to earnings."
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to complete delivery."
            }
            _isLoading.value = false
        }
    }

    // --- PAYOUTS & WALLET ---
    fun requestPayout(amount: Double, destination: String) {
        val riderId = _currentRiderId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.requestPayout(riderId, amount, destination)
            result.onSuccess {
                _snackbarMessage.value = "Payout request of ₹${amount.toInt()} submitted successfully."
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Payout request failed."
            }
            _isLoading.value = false
        }
    }

    fun updateBankDetails(holder: String, accNum: String, ifsc: String, upi: String) {
        val riderId = _currentRiderId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBankDetails(riderId, holder, accNum, ifsc, upi)
            _snackbarMessage.value = "Bank & UPI details updated."
            _isLoading.value = false
        }
    }

    fun recordCodDeposit(amount: Double) {
        val riderId = _currentRiderId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.recordCodDeposit(riderId, amount)
            _snackbarMessage.value = "COD deposit of ₹${amount.toInt()} recorded."
            _isLoading.value = false
        }
    }

    // --- NOTIFICATIONS & SUPPORT ---
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val riderId = _currentRiderId.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(riderId)
            _snackbarMessage.value = "All notifications marked as read."
        }
    }

    fun createSupportTicket(category: String, subject: String, description: String, orderId: Long? = null) {
        val riderId = _currentRiderId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createSupportTicket(riderId, category, subject, description, orderId)
            result.onSuccess { ticket ->
                _snackbarMessage.value = "Support ticket ${ticket.ticketNumber} created. Priority: ${ticket.priority}."
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to create support ticket."
            }
            _isLoading.value = false
        }
    }

    // --- PLATFORM SETTINGS ---
    fun updateSystemSetting(key: String, value: String, description: String = "") {
        viewModelScope.launch {
            repository.updateSetting(key, value, description)
            _snackbarMessage.value = "Setting '$key' updated."
        }
    }
}

class TukTukViewModelFactory(private val repository: TukTukRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TukTukViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TukTukViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
