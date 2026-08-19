package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TukTukDao {
    // --- USER & AUTH ---
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- RIDER ---
    @Query("SELECT * FROM riders WHERE userId = :userId LIMIT 1")
    suspend fun getRiderByUserId(userId: Long): RiderEntity?

    @Query("SELECT * FROM riders WHERE id = :riderId LIMIT 1")
    fun getRiderFlow(riderId: Long): Flow<RiderEntity?>

    @Query("SELECT * FROM riders WHERE id = :riderId LIMIT 1")
    suspend fun getRiderById(riderId: Long): RiderEntity?

    @Query("SELECT * FROM riders ORDER BY id ASC LIMIT 1")
    suspend fun getFirstRider(): RiderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRider(rider: RiderEntity): Long

    @Update
    suspend fun updateRider(rider: RiderEntity)

    @Query("UPDATE riders SET isOnline = :isOnline WHERE id = :riderId")
    suspend fun setRiderOnlineStatus(riderId: Long, isOnline: Boolean)

    @Query("UPDATE riders SET verificationStatus = :status WHERE id = :riderId")
    suspend fun updateRiderVerificationStatus(riderId: Long, status: String)

    // --- DOCUMENTS ---
    @Query("SELECT * FROM rider_documents WHERE riderId = :riderId ORDER BY id ASC")
    fun getRiderDocumentsFlow(riderId: Long): Flow<List<RiderDocumentEntity>>

    @Query("SELECT * FROM rider_documents WHERE riderId = :riderId")
    suspend fun getRiderDocuments(riderId: Long): List<RiderDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: RiderDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<RiderDocumentEntity>)

    @Update
    suspend fun updateDocument(doc: RiderDocumentEntity)

    // --- RESTAURANTS & CUSTOMERS ---
    @Query("SELECT * FROM restaurants")
    suspend fun getAllRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE id = :id LIMIT 1")
    suspend fun getRestaurantById(id: Long): RestaurantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<RestaurantEntity>)

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    // --- ORDERS ---
    @Query("SELECT * FROM orders WHERE riderId = :riderId ORDER BY createdAt DESC")
    fun getOrdersForRiderFlow(riderId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE riderId = :riderId AND status NOT IN ('DELIVERED', 'CANCELLED', 'FAILED') LIMIT 1")
    fun getActiveOrderFlow(riderId: Long): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE status = 'ASSIGNED' AND riderId = :riderId LIMIT 1")
    fun getAssignedIncomingOrderFlow(riderId: Long): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Long): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderFlow(orderId: Long): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // --- ORDER ITEMS ---
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsFlow(orderId: Long): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // --- ORDER STATUS HISTORY ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(history: OrderStatusHistoryEntity)

    @Query("SELECT * FROM order_status_history WHERE orderId = :orderId ORDER BY timestamp ASC")
    suspend fun getOrderStatusHistory(orderId: Long): List<OrderStatusHistoryEntity>

    // --- EARNINGS ---
    @Query("SELECT * FROM earnings WHERE riderId = :riderId ORDER BY timestamp DESC")
    fun getEarningsFlow(riderId: Long): Flow<List<EarningEntity>>

    @Query("SELECT * FROM earnings WHERE riderId = :riderId")
    suspend fun getEarnings(riderId: Long): List<EarningEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarning(earning: EarningEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarnings(earnings: List<EarningEntity>)

    // --- PAYOUTS ---
    @Query("SELECT * FROM payouts WHERE riderId = :riderId ORDER BY requestedAt DESC")
    fun getPayoutsFlow(riderId: Long): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayouts(payouts: List<PayoutEntity>)

    // --- NOTIFICATIONS ---
    @Query("SELECT * FROM notifications WHERE riderId = :riderId ORDER BY timestamp DESC")
    fun getNotificationsFlow(riderId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationRead(notificationId: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE riderId = :riderId")
    suspend fun markAllNotificationsRead(riderId: Long)

    // --- SUPPORT TICKETS ---
    @Query("SELECT * FROM support_tickets WHERE riderId = :riderId ORDER BY createdAt DESC")
    fun getSupportTicketsFlow(riderId: Long): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity): Long

    // --- SYSTEM SETTINGS ---
    @Query("SELECT * FROM system_settings")
    fun getSystemSettingsFlow(): Flow<List<SystemSettingEntity>>

    @Query("SELECT * FROM system_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSystemSetting(key: String): SystemSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SystemSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<SystemSettingEntity>)
}
