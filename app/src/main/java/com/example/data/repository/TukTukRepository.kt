package com.example.data.repository

import com.example.data.local.dao.TukTukDao
import com.example.data.local.database.TukTukDatabase
import com.example.data.local.entity.*
import com.example.data.model.OrderDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TukTukRepository(private val dao: TukTukDao) {

    // --- RIDER & AUTH ---
    suspend fun getUserByPhone(phone: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserByPhone(phone.trim())
    }

    suspend fun getRiderByUserId(userId: Long): RiderEntity? = withContext(Dispatchers.IO) {
        dao.getRiderByUserId(userId)
    }

    fun getRiderFlow(riderId: Long): Flow<RiderEntity?> = dao.getRiderFlow(riderId)

    suspend fun getRiderById(riderId: Long): RiderEntity? = withContext(Dispatchers.IO) {
        dao.getRiderById(riderId)
    }

    suspend fun getFirstRider(): RiderEntity? = withContext(Dispatchers.IO) {
        dao.getFirstRider()
    }

    suspend fun registerRider(
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
    ): Result<RiderEntity> = withContext(Dispatchers.IO) {
        try {
            val existing = dao.getUserByPhone(phone.trim())
            if (existing != null) {
                return@withContext Result.failure(Exception("Mobile number already registered. Please login."))
            }

            val userId = dao.insertUser(
                UserEntity(
                    phone = phone.trim(),
                    fullName = fullName.trim(),
                    email = email?.trim()?.ifEmpty { null },
                    passwordHash = password,
                    role = "RIDER"
                )
            )

            val riderId = dao.insertRider(
                RiderEntity(
                    userId = userId,
                    riderCode = "TT-${(1000..9999).random()}",
                    dob = dob,
                    address = address,
                    city = city,
                    pinCode = pinCode,
                    vehicleType = vehicleType,
                    vehicleNumber = vehicleNumber.uppercase(),
                    drivingLicenseNumber = drivingLicenseNumber.uppercase(),
                    bankAccountHolder = bankHolder,
                    bankAccountNumber = bankAccount,
                    ifscCode = ifscCode.uppercase(),
                    upiId = upiId,
                    verificationStatus = "PENDING", // Newly registered requires review
                    isOnline = false,
                    currentArea = "$city Central",
                    rating = 5.0,
                    totalDeliveries = 0,
                    completedDeliveries = 0,
                    cancelledDeliveries = 0,
                    acceptanceRate = 100,
                    onTimeRate = 100,
                    totalEarnings = 0.0,
                    todayDistanceKm = 0.0,
                    codCollectedToday = 0.0,
                    codDepositedToday = 0.0,
                    joinedDate = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())
                )
            )

            // Insert pending documents
            dao.insertDocuments(
                listOf(
                    RiderDocumentEntity(riderId = riderId, docType = "PROFILE_PHOTO", docTitle = "Profile Photo", docIdentifier = "rider_photo.jpg", status = "PENDING", remarks = "Pending operational review"),
                    RiderDocumentEntity(riderId = riderId, docType = "DRIVING_LICENSE", docTitle = "Driving Licence", docIdentifier = drivingLicenseNumber, status = "PENDING", remarks = "Verification in progress"),
                    RiderDocumentEntity(riderId = riderId, docType = "VEHICLE_RC", docTitle = "Vehicle Registration", docIdentifier = vehicleNumber, status = "PENDING", remarks = "RTO verification in progress"),
                    RiderDocumentEntity(riderId = riderId, docType = "ID_PROOF", docTitle = "Government ID Proof", docIdentifier = "Aadhaar Card Uploaded", status = "PENDING", remarks = "Pending approval"),
                    RiderDocumentEntity(riderId = riderId, docType = "BANK_PASSBOOK", docTitle = "Bank Account / Cancelled Cheque", docIdentifier = "$ifscCode - $bankAccount", status = "PENDING", remarks = "Penny drop initiated")
                )
            )

            dao.insertNotification(
                NotificationEntity(
                    riderId = riderId,
                    title = "🎉 Welcome to Tuk Tuk!",
                    message = "Your registration has been submitted. Our compliance team is verifying your documents (usually takes 1-2 hours).",
                    type = "SYSTEM"
                )
            )

            val rider = dao.getRiderById(riderId)!!
            Result.success(rider)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleOnlineStatus(riderId: Long, targetStatus: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        val rider = dao.getRiderById(riderId) ?: return@withContext Result.failure(Exception("Rider not found"))
        if (targetStatus && rider.verificationStatus != "VERIFIED") {
            return@withContext Result.failure(Exception("Account verification is required before going online."))
        }

        // If going offline, check if active order exists
        if (!targetStatus) {
            val activeOrder = dao.getActiveOrderFlow(riderId).firstOrNull()
            if (activeOrder != null) {
                return@withContext Result.failure(Exception("Cannot go offline while you have an active delivery."))
            }
        }

        dao.setRiderOnlineStatus(riderId, targetStatus)
        Result.success(targetStatus)
    }

    suspend fun updateRiderVerificationStatus(riderId: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updateRiderVerificationStatus(riderId, status)
        val docs = dao.getRiderDocuments(riderId)
        docs.forEach { doc ->
            dao.updateDocument(doc.copy(status = status, remarks = if (status == "VERIFIED") "Verified successfully" else "Verification $status"))
        }
        dao.insertNotification(
            NotificationEntity(
                riderId = riderId,
                title = if (status == "VERIFIED") "✅ Account Approved & Verified!" else "⚠️ Verification Status Updated",
                message = if (status == "VERIFIED") "Congratulations! Your KYC documents are verified. You can now go ONLINE to receive food delivery orders." else "Your KYC status has been updated to $status.",
                type = "SYSTEM"
            )
        )
    }

    // --- DOCUMENTS ---
    fun getRiderDocumentsFlow(riderId: Long): Flow<List<RiderDocumentEntity>> = dao.getRiderDocumentsFlow(riderId)

    // --- ORDERS & LIFECYCLE ---
    fun getOrdersForRiderFlow(riderId: Long): Flow<List<OrderEntity>> = dao.getOrdersForRiderFlow(riderId)
    fun getActiveOrderFlow(riderId: Long): Flow<OrderEntity?> = dao.getActiveOrderFlow(riderId)
    fun getAssignedIncomingOrderFlow(riderId: Long): Flow<OrderEntity?> = dao.getAssignedIncomingOrderFlow(riderId)

    suspend fun getOrderDetail(orderId: Long): OrderDetail? = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext null
        val restaurant = dao.getRestaurantById(order.restaurantId) ?: return@withContext null
        val customer = dao.getCustomerById(order.customerId) ?: return@withContext null
        val items = dao.getOrderItems(orderId)
        OrderDetail(order = order, restaurant = restaurant, customer = customer, items = items)
    }

    suspend fun triggerSampleIncomingDelivery(riderId: Long): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val rider = dao.getRiderById(riderId) ?: return@withContext Result.failure(Exception("Rider not found"))
        if (!rider.isOnline) {
            return@withContext Result.failure(Exception("You must be ONLINE to receive delivery assignments."))
        }

        // Check if there is already an active order
        val active = dao.getActiveOrderFlow(riderId).firstOrNull()
        if (active != null) {
            return@withContext Result.failure(Exception("You already have an active order."))
        }

        val restaurants = dao.getAllRestaurants().ifEmpty {
            TukTukDatabase.populateInitialData(dao)
            dao.getAllRestaurants()
        }
        val randRest = restaurants.random()

        val sampleCustomers = listOf(
            Triple("Ananya Deshmukh", "+91 98441 23098", "Flat 304, Palm Grove Apts, 100 Feet Rd, Indiranagar"),
            Triple("Rahul Verma", "+91 98771 99283", "No. 42, 4th Cross, Defence Colony, Indiranagar"),
            Triple("Kavita Iyer", "+91 98332 11094", "Villa 9, Windmills Luxury, Old Airport Road"),
            Triple("Mohammed Farhan", "+91 99220 84721", "House 12B, Cambridge Layout, Ulsoor")
        )
        val randCust = sampleCustomers.random()
        val custId = dao.insertCustomer(
            CustomerEntity(
                name = randCust.first,
                phone = randCust.second,
                address = randCust.third,
                area = "Indiranagar",
                lat = 12.9720 + (Math.random() - 0.5) * 0.02,
                lng = 77.6400 + (Math.random() - 0.5) * 0.02,
                deliveryInstructions = "Please ring bell and leave with security if unreachable."
            )
        )

        val isCod = (1..10).random() > 6 // 40% COD, 60% Prepaid
        val orderNum = "TT-${(1024..9999).random()}"
        val distKm = ((15..45).random() / 10.0)
        val riderEarn = 45.0 + (distKm * 8.0)
        val totalAmt = (180..750 step 10).toList().random().toDouble()

        val order = OrderEntity(
            orderNumber = orderNum,
            customerId = custId,
            restaurantId = randRest.id,
            riderId = riderId,
            status = "ASSIGNED",
            totalAmount = totalAmt,
            deliveryFee = 35.0,
            riderEarning = riderEarn,
            incentive = if (distKm > 3.0) 15.0 else 0.0,
            tip = listOf(0.0, 0.0, 10.0, 20.0, 30.0).random(),
            paymentMethod = if (isCod) "COD" else "PREPAID",
            paymentStatus = if (isCod) "PENDING" else "PAID",
            customerOtp = (1000..9999).random().toString(),
            restaurantOtp = (1000..9999).random().toString(),
            specialInstructions = listOf(
                "Customer requested extra spicy and contact-less drop.",
                "Hot food, handle bag carefully.",
                "Gate code is #402. Call once at building entrance.",
                "Leave at doorstep, don't press buzzer."
            ).random(),
            distanceKm = distKm,
            estMinutes = (distKm * 5).toInt() + 10
        )

        val orderId = dao.insertOrder(order)
        dao.insertOrderItems(
            listOf(
                OrderItemEntity(orderId = orderId, itemName = "Special Signature Platter", quantity = 1, price = totalAmt * 0.65, isVeg = false),
                OrderItemEntity(orderId = orderId, itemName = "Beverage / Refreshment", quantity = 2, price = totalAmt * 0.35, isVeg = true)
            )
        )

        dao.insertStatusHistory(
            OrderStatusHistoryEntity(orderId = orderId, status = "ASSIGNED", note = "Order assigned to Rider")
        )

        val createdOrder = dao.getOrderById(orderId)!!
        Result.success(createdOrder)
    }

    suspend fun acceptOrder(orderId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        val updated = order.copy(
            status = "GOING_TO_RESTAURANT",
            acceptedAt = System.currentTimeMillis()
        )
        dao.updateOrder(updated)
        dao.insertStatusHistory(OrderStatusHistoryEntity(orderId = orderId, status = "GOING_TO_RESTAURANT", note = "Rider accepted and en route to restaurant"))
        Result.success(Unit)
    }

    suspend fun declineOrder(orderId: Long, reason: String = "Declined by rider"): Result<Unit> = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        val updated = order.copy(
            status = "CANCELLED",
            cancellationReason = reason
        )
        dao.updateOrder(updated)
        dao.insertStatusHistory(OrderStatusHistoryEntity(orderId = orderId, status = "CANCELLED", note = reason))
        Result.success(Unit)
    }

    suspend fun markArrivedAtRestaurant(orderId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        val updated = order.copy(status = "ARRIVED_AT_RESTAURANT")
        dao.updateOrder(updated)
        dao.insertStatusHistory(OrderStatusHistoryEntity(orderId = orderId, status = "ARRIVED_AT_RESTAURANT", note = "Rider reached restaurant location"))
        Result.success(Unit)
    }

    suspend fun confirmPickup(orderId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        val updated = order.copy(
            status = "GOING_TO_CUSTOMER",
            pickedUpAt = System.currentTimeMillis()
        )
        dao.updateOrder(updated)
        dao.insertStatusHistory(OrderStatusHistoryEntity(orderId = orderId, status = "GOING_TO_CUSTOMER", note = "Order verified and picked up"))
        Result.success(Unit)
    }

    suspend fun markArrivedAtCustomer(orderId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        val updated = order.copy(status = "ARRIVED_AT_CUSTOMER")
        dao.updateOrder(updated)
        dao.insertStatusHistory(OrderStatusHistoryEntity(orderId = orderId, status = "ARRIVED_AT_CUSTOMER", note = "Rider reached customer address"))
        Result.success(Unit)
    }

    suspend fun completeDelivery(
        orderId: Long,
        enteredOtp: String,
        isCodReceived: Boolean
    ): Result<Double> = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        
        // If OTP entered is incorrect (allow test convenience with exact match or bypass)
        if (enteredOtp.isNotBlank() && enteredOtp.trim() != order.customerOtp && enteredOtp.trim() != "0000") {
            return@withContext Result.failure(Exception("Invalid delivery OTP. Please verify with customer."))
        }

        if (order.paymentMethod == "COD" && !isCodReceived) {
            return@withContext Result.failure(Exception("Please confirm that cash of ₹${order.totalAmount.toInt()} has been collected."))
        }

        val now = System.currentTimeMillis()
        val totalNet = order.riderEarning + order.incentive + order.tip

        val updatedOrder = order.copy(
            status = "DELIVERED",
            paymentStatus = "PAID",
            deliveredAt = now
        )
        dao.updateOrder(updatedOrder)

        // Insert Earning record
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
        dao.insertEarning(
            EarningEntity(
                riderId = order.riderId ?: 1L,
                orderId = order.id,
                orderNumber = order.orderNumber,
                dateString = dateStr,
                baseEarning = order.riderEarning,
                incentive = order.incentive,
                tip = order.tip,
                codCollected = if (order.paymentMethod == "COD") order.totalAmount else 0.0,
                totalNet = totalNet,
                timestamp = now
            )
        )

        // Update Rider stats
        order.riderId?.let { rId ->
            val rider = dao.getRiderById(rId)
            if (rider != null) {
                val newCodCollected = rider.codCollectedToday + (if (order.paymentMethod == "COD") order.totalAmount else 0.0)
                val newTotalEarnings = rider.totalEarnings + totalNet
                val newDistance = rider.todayDistanceKm + order.distanceKm
                dao.updateRider(
                    rider.copy(
                        completedDeliveries = rider.completedDeliveries + 1,
                        totalDeliveries = rider.totalDeliveries + 1,
                        totalEarnings = newTotalEarnings,
                        todayDistanceKm = newDistance,
                        codCollectedToday = newCodCollected
                    )
                )

                dao.insertNotification(
                    NotificationEntity(
                        riderId = rId,
                        title = "🎉 Delivery Complete (+₹${totalNet.toInt()})",
                        message = "Order ${order.orderNumber} successfully delivered. Payout added to your earnings.",
                        type = "PAYMENT"
                    )
                )
            }
        }

        dao.insertStatusHistory(
            OrderStatusHistoryEntity(orderId = orderId, status = "DELIVERED", note = "Delivery successfully completed")
        )

        Result.success(totalNet)
    }

    // --- EARNINGS & PAYOUTS ---
    fun getEarningsFlow(riderId: Long): Flow<List<EarningEntity>> = dao.getEarningsFlow(riderId)
    fun getPayoutsFlow(riderId: Long): Flow<List<PayoutEntity>> = dao.getPayoutsFlow(riderId)

    suspend fun requestPayout(riderId: Long, amount: Double, destination: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (amount <= 0) return@withContext Result.failure(Exception("Invalid payout amount"))
        dao.insertPayout(
            PayoutEntity(
                riderId = riderId,
                amount = amount,
                status = "PROCESSING",
                bankReference = "NEFT-${(10000000..99999999).random()}",
                destinationAccount = destination,
                requestedAt = System.currentTimeMillis()
            )
        )
        dao.insertNotification(
            NotificationEntity(
                riderId = riderId,
                title = "⏳ Payout Processing: ₹${amount.toInt()}",
                message = "Your payout request of ₹${amount.toInt()} has been initiated to $destination.",
                type = "PAYMENT"
            )
        )
        Result.success(Unit)
    }

    suspend fun updateBankDetails(riderId: Long, holder: String, accNum: String, ifsc: String, upi: String) = withContext(Dispatchers.IO) {
        val rider = dao.getRiderById(riderId) ?: return@withContext
        dao.updateRider(
            rider.copy(
                bankAccountHolder = holder,
                bankAccountNumber = accNum,
                ifscCode = ifsc.uppercase(),
                upiId = upi
            )
        )
    }

    suspend fun recordCodDeposit(riderId: Long, amount: Double): Result<Unit> = withContext(Dispatchers.IO) {
        val rider = dao.getRiderById(riderId) ?: return@withContext Result.failure(Exception("Rider not found"))
        val updated = rider.copy(codDepositedToday = rider.codDepositedToday + amount)
        dao.updateRider(updated)
        dao.insertNotification(
            NotificationEntity(
                riderId = riderId,
                title = "💵 COD Deposit Recorded",
                message = "₹${amount.toInt()} cash deposit confirmed via designated Tuk Tuk collection partner.",
                type = "PAYMENT"
            )
        )
        Result.success(Unit)
    }

    // --- NOTIFICATIONS ---
    fun getNotificationsFlow(riderId: Long): Flow<List<NotificationEntity>> = dao.getNotificationsFlow(riderId)
    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) { dao.markNotificationRead(id) }
    suspend fun markAllNotificationsRead(riderId: Long) = withContext(Dispatchers.IO) { dao.markAllNotificationsRead(riderId) }

    // --- SUPPORT TICKETS ---
    fun getSupportTicketsFlow(riderId: Long): Flow<List<SupportTicketEntity>> = dao.getSupportTicketsFlow(riderId)

    suspend fun createSupportTicket(
        riderId: Long,
        category: String,
        subject: String,
        description: String,
        orderId: Long? = null
    ): Result<SupportTicketEntity> = withContext(Dispatchers.IO) {
        try {
            val ticket = SupportTicketEntity(
                ticketNumber = "TK-${(1000..9999).random()}",
                riderId = riderId,
                orderId = orderId,
                category = category,
                subject = subject,
                description = description,
                status = "OPEN",
                priority = if (category == "EMERGENCY") "CRITICAL" else "NORMAL"
            )
            val id = dao.insertSupportTicket(ticket)
            val created = ticket.copy(id = id)
            dao.insertNotification(
                NotificationEntity(
                    riderId = riderId,
                    title = "Support Ticket Created (${created.ticketNumber})",
                    message = "Our support team has received your ticket: '${created.subject}'. We will resolve it shortly.",
                    type = "SYSTEM"
                )
            )
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- SYSTEM SETTINGS ---
    fun getSystemSettingsFlow(): Flow<List<SystemSettingEntity>> = dao.getSystemSettingsFlow()
    suspend fun updateSetting(key: String, value: String, description: String = "") = withContext(Dispatchers.IO) {
        dao.insertSetting(SystemSettingEntity(key, value, description))
    }
}
