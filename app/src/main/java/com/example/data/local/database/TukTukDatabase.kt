package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.TukTukDao
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        RiderEntity::class,
        RiderDocumentEntity::class,
        RestaurantEntity::class,
        CustomerEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        OrderStatusHistoryEntity::class,
        EarningEntity::class,
        PayoutEntity::class,
        NotificationEntity::class,
        SupportTicketEntity::class,
        SystemSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TukTukDatabase : RoomDatabase() {
    abstract fun tukTukDao(): TukTukDao

    companion object {
        @Volatile
        private var INSTANCE: TukTukDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): TukTukDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TukTukDatabase::class.java,
                    "tuktuk_delivery_rider.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.tukTukDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: TukTukDao) {
            // 1. Initial User
            val userId = dao.insertUser(
                UserEntity(
                    id = 1,
                    phone = "9876543210",
                    fullName = "Ravi Kumar",
                    email = "ravi.kumar@tuktuk.in",
                    passwordHash = "password123",
                    role = "RIDER"
                )
            )

            // 2. Initial Rider Profile
            val riderId = dao.insertRider(
                RiderEntity(
                    id = 1,
                    userId = userId,
                    riderCode = "TT-7842",
                    dob = "1998-05-14",
                    address = "12th Main, HAL 2nd Stage, Indiranagar",
                    city = "Bengaluru",
                    pinCode = "560038",
                    vehicleType = "Motorcycle",
                    vehicleNumber = "KA 03 HM 4892",
                    drivingLicenseNumber = "DL-0420190014298",
                    bankAccountHolder = "Ravi Kumar",
                    bankAccountNumber = "501002348912",
                    ifscCode = "HDFC0001234",
                    upiId = "ravi.kumar@upi",
                    verificationStatus = "VERIFIED",
                    isOnline = false,
                    currentArea = "Indiranagar, Bengaluru",
                    rating = 4.85,
                    totalDeliveries = 142,
                    completedDeliveries = 138,
                    cancelledDeliveries = 4,
                    acceptanceRate = 96,
                    onTimeRate = 98,
                    totalEarnings = 18450.0,
                    todayDistanceKm = 18.5,
                    codCollectedToday = 840.0,
                    codDepositedToday = 600.0,
                    joinedDate = "Nov 2025"
                )
            )

            // 3. Documents
            dao.insertDocuments(
                listOf(
                    RiderDocumentEntity(
                        riderId = riderId,
                        docType = "PROFILE_PHOTO",
                        docTitle = "Rider Profile Photo",
                        docIdentifier = "ravi_profile_photo.jpg",
                        status = "VERIFIED",
                        remarks = "Face matches government ID"
                    ),
                    RiderDocumentEntity(
                        riderId = riderId,
                        docType = "DRIVING_LICENSE",
                        docTitle = "Driving Licence (Two Wheeler)",
                        docIdentifier = "DL-0420190014298",
                        status = "VERIFIED",
                        remarks = "Valid till 2039"
                    ),
                    RiderDocumentEntity(
                        riderId = riderId,
                        docType = "VEHICLE_RC",
                        docTitle = "Vehicle Registration (RC)",
                        docIdentifier = "KA 03 HM 4892",
                        status = "VERIFIED",
                        remarks = "Hero Splendor Plus - Active Fitness"
                    ),
                    RiderDocumentEntity(
                        riderId = riderId,
                        docType = "ID_PROOF",
                        docTitle = "Aadhaar Card",
                        docIdentifier = "XXXX-XXXX-8921",
                        status = "VERIFIED",
                        remarks = "UIDAI Aadhaar Verified"
                    ),
                    RiderDocumentEntity(
                        riderId = riderId,
                        docType = "BANK_PASSBOOK",
                        docTitle = "Bank Account & IFSC",
                        docIdentifier = "HDFC0001234 - 501002348912",
                        status = "VERIFIED",
                        remarks = "Penny drop verification successful"
                    )
                )
            )

            // 4. Restaurants
            val r1 = dao.insertRestaurant(
                RestaurantEntity(
                    id = 1,
                    name = "Paradise Biryani Hub",
                    address = "100 Feet Rd, Indiranagar, Bengaluru",
                    area = "Indiranagar",
                    phone = "9845012345",
                    cuisine = "Hyderabadi Biryani & Mughlai",
                    lat = 12.9784,
                    lng = 77.6408,
                    rating = 4.7
                )
            )
            val r2 = dao.insertRestaurant(
                RestaurantEntity(
                    id = 2,
                    name = "Punjab Grill Express",
                    address = "12th Main Rd, HAL 2nd Stage, Indiranagar",
                    area = "Indiranagar",
                    phone = "9845098765",
                    cuisine = "North Indian, Tandoori & Parathas",
                    lat = 12.9716,
                    lng = 77.6412,
                    rating = 4.6
                )
            )
            val r3 = dao.insertRestaurant(
                RestaurantEntity(
                    id = 3,
                    name = "Dosa Corner & Filter Coffee",
                    address = "CMH Road, Indiranagar, Bengaluru",
                    area = "CMH Road",
                    phone = "9845055443",
                    cuisine = "South Indian, Dosa & Snacks",
                    lat = 12.9798,
                    lng = 77.6321,
                    rating = 4.8
                )
            )
            val r4 = dao.insertRestaurant(
                RestaurantEntity(
                    id = 4,
                    name = "Chai Point & Quick Bites",
                    address = "Old Airport Rd, Kodihalli, Bengaluru",
                    area = "Old Airport Road",
                    phone = "9845066778",
                    cuisine = "Tea, Samosa, Sandwiches",
                    lat = 12.9602,
                    lng = 77.6480,
                    rating = 4.4
                )
            )

            // 5. Customers
            val c1 = dao.insertCustomer(
                CustomerEntity(
                    id = 1,
                    name = "Aarav Sharma",
                    phone = "+91 98112 34567",
                    address = "Flat 402, Green Glen Heights, 6th Cross, Indiranagar",
                    area = "Indiranagar",
                    landmark = "Near BDA Complex",
                    lat = 12.9734,
                    lng = 77.6432,
                    deliveryInstructions = "Ring bell twice, leave food on door shoe-rack if on call."
                )
            )
            val c2 = dao.insertCustomer(
                CustomerEntity(
                    id = 2,
                    name = "Sneha Patel",
                    phone = "+91 98223 45678",
                    address = "Villa 18, Palm Meadows, Kodihalli",
                    area = "Kodihalli",
                    landmark = "Opposite Leela Palace",
                    lat = 12.9620,
                    lng = 77.6492,
                    deliveryInstructions = "Please call when you reach security gate. Tower B elevator."
                )
            )
            val c3 = dao.insertCustomer(
                CustomerEntity(
                    id = 3,
                    name = "Vikram Reddy",
                    phone = "+91 98334 56789",
                    address = "Apt 201, Sunrise Residency, CMH Road",
                    area = "CMH Road",
                    landmark = "Behind Metro Station",
                    lat = 12.9810,
                    lng = 77.6350,
                    deliveryInstructions = "Do not ring bell, baby is sleeping. Text on arrival."
                )
            )

            // 6. Past Orders for History
            val o1Id = dao.insertOrder(
                OrderEntity(
                    orderNumber = "TT-1021",
                    customerId = c1,
                    restaurantId = r1,
                    riderId = riderId,
                    status = "DELIVERED",
                    totalAmount = 520.0,
                    deliveryFee = 35.0,
                    riderEarning = 56.0,
                    incentive = 15.0,
                    tip = 20.0,
                    paymentMethod = "PREPAID",
                    paymentStatus = "PAID",
                    customerOtp = "4829",
                    restaurantOtp = "1923",
                    specialInstructions = "Please include extra mint chutney and cutlery.",
                    distanceKm = 2.8,
                    estMinutes = 15,
                    createdAt = System.currentTimeMillis() - 7200000,
                    acceptedAt = System.currentTimeMillis() - 7000000,
                    pickedUpAt = System.currentTimeMillis() - 5800000,
                    deliveredAt = System.currentTimeMillis() - 4800000
                )
            )
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(orderId = o1Id, itemName = "Royal Mutton Biryani (Handi)", quantity = 1, price = 360.0, isVeg = false),
                    OrderItemEntity(orderId = o1Id, itemName = "Mirchi Ka Salan", quantity = 1, price = 70.0, isVeg = true),
                    OrderItemEntity(orderId = o1Id, itemName = "Gulab Jamun (2 pcs)", quantity = 1, price = 90.0, isVeg = true)
                )
            )

            val o2Id = dao.insertOrder(
                OrderEntity(
                    orderNumber = "TT-1022",
                    customerId = c2,
                    restaurantId = r2,
                    riderId = riderId,
                    status = "DELIVERED",
                    totalAmount = 640.0,
                    deliveryFee = 40.0,
                    riderEarning = 62.0,
                    incentive = 20.0,
                    tip = 10.0,
                    paymentMethod = "COD",
                    paymentStatus = "PAID",
                    customerOtp = "6312",
                    restaurantOtp = "4410",
                    specialInstructions = "Collect ₹640 Cash from customer on delivery.",
                    distanceKm = 3.6,
                    estMinutes = 20,
                    createdAt = System.currentTimeMillis() - 14400000,
                    acceptedAt = System.currentTimeMillis() - 14200000,
                    pickedUpAt = System.currentTimeMillis() - 13000000,
                    deliveredAt = System.currentTimeMillis() - 12000000
                )
            )
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(orderId = o2Id, itemName = "Butter Paneer Masala", quantity = 1, price = 280.0, isVeg = true),
                    OrderItemEntity(orderId = o2Id, itemName = "Butter Naan", quantity = 3, price = 150.0, isVeg = true),
                    OrderItemEntity(orderId = o2Id, itemName = "Dal Makhani", quantity = 1, price = 210.0, isVeg = true)
                )
            )

            val o3Id = dao.insertOrder(
                OrderEntity(
                    orderNumber = "TT-1023",
                    customerId = c3,
                    restaurantId = r3,
                    riderId = riderId,
                    status = "DELIVERED",
                    totalAmount = 310.0,
                    deliveryFee = 25.0,
                    riderEarning = 45.0,
                    incentive = 10.0,
                    tip = 0.0,
                    paymentMethod = "PREPAID",
                    paymentStatus = "PAID",
                    customerOtp = "9104",
                    restaurantOtp = "7291",
                    specialInstructions = "Crispy dosas with extra sambar requested.",
                    distanceKm = 2.1,
                    estMinutes = 12,
                    createdAt = System.currentTimeMillis() - 28800000,
                    acceptedAt = System.currentTimeMillis() - 28600000,
                    pickedUpAt = System.currentTimeMillis() - 27500000,
                    deliveredAt = System.currentTimeMillis() - 26700000
                )
            )
            dao.insertOrderItems(
                listOf(
                    OrderItemEntity(orderId = o3Id, itemName = "Ghee Roast Masala Dosa", quantity = 2, price = 220.0, isVeg = true),
                    OrderItemEntity(orderId = o3Id, itemName = "Filter Coffee (Hot)", quantity = 2, price = 90.0, isVeg = true)
                )
            )

            // 7. Earnings
            dao.insertEarnings(
                listOf(
                    EarningEntity(
                        riderId = riderId,
                        orderId = o1Id,
                        orderNumber = "TT-1021",
                        dateString = "Today",
                        baseEarning = 56.0,
                        incentive = 15.0,
                        tip = 20.0,
                        totalNet = 91.0,
                        timestamp = System.currentTimeMillis() - 4800000
                    ),
                    EarningEntity(
                        riderId = riderId,
                        orderId = o2Id,
                        orderNumber = "TT-1022",
                        dateString = "Today",
                        baseEarning = 62.0,
                        incentive = 20.0,
                        tip = 10.0,
                        codCollected = 640.0,
                        totalNet = 92.0,
                        timestamp = System.currentTimeMillis() - 12000000
                    ),
                    EarningEntity(
                        riderId = riderId,
                        orderId = o3Id,
                        orderNumber = "TT-1023",
                        dateString = "Today",
                        baseEarning = 45.0,
                        incentive = 10.0,
                        tip = 0.0,
                        totalNet = 55.0,
                        timestamp = System.currentTimeMillis() - 26700000
                    )
                )
            )

            // 8. Payouts
            dao.insertPayouts(
                listOf(
                    PayoutEntity(
                        riderId = riderId,
                        amount = 4250.0,
                        status = "PAID",
                        bankReference = "NEFT-78192348",
                        destinationAccount = "HDFC Bank (..4912)",
                        requestedAt = System.currentTimeMillis() - 86400000 * 3,
                        processedAt = System.currentTimeMillis() - 86400000 * 3 + 3600000
                    ),
                    PayoutEntity(
                        riderId = riderId,
                        amount = 3800.0,
                        status = "PAID",
                        bankReference = "UPI-994821034",
                        destinationAccount = "ravi.kumar@upi",
                        requestedAt = System.currentTimeMillis() - 86400000 * 7,
                        processedAt = System.currentTimeMillis() - 86400000 * 7 + 1800000
                    )
                )
            )

            // 9. Notifications
            dao.insertNotifications(
                listOf(
                    NotificationEntity(
                        riderId = riderId,
                        title = "🚀 Evening Surge Bonus Active",
                        message = "Earn extra ₹20 per completed delivery between 7:00 PM and 11:00 PM in Indiranagar & Koramangala.",
                        type = "INCENTIVE",
                        isRead = false,
                        timestamp = System.currentTimeMillis() - 3600000
                    ),
                    NotificationEntity(
                        riderId = riderId,
                        title = "✅ Documents Approved",
                        message = "Your driving licence and vehicle RC have been successfully verified by the Tuk Tuk onboarding team.",
                        type = "SYSTEM",
                        isRead = true,
                        timestamp = System.currentTimeMillis() - 86400000
                    ),
                    NotificationEntity(
                        riderId = riderId,
                        title = "💰 Weekly Payout Deposited",
                        message = "₹4,250 has been credited to your HDFC Bank account (Ref: NEFT-78192348).",
                        type = "PAYMENT",
                        isRead = true,
                        timestamp = System.currentTimeMillis() - 86400000 * 3
                    )
                )
            )

            // 10. System Settings
            dao.insertSettings(
                listOf(
                    SystemSettingEntity("merchant_upi_id", "tuktuk.pay@okaxis", "Configured Merchant UPI ID for payments"),
                    SystemSettingEntity("merchant_name", "Tuk Tuk Food Delivery Pvt Ltd", "Registered business legal name"),
                    SystemSettingEntity("payment_gateway", "Razorpay / Cashfree UPI Gateway", "Active payment processing provider"),
                    SystemSettingEntity("rider_base_earning", "45", "Base payout per delivery (₹)"),
                    SystemSettingEntity("per_km_rate", "9", "Additional pay per km after 2km (₹)"),
                    SystemSettingEntity("surge_multiplier", "1.2", "Active peak hour surge multiplier"),
                    SystemSettingEntity("cod_limit", "2500", "Maximum outstanding COD cash allowed before deposit"),
                    SystemSettingEntity("emergency_helpline", "1800-885-8855", "24/7 Rider Roadside & Safety Helpline")
                )
            )
        }
    }
}
