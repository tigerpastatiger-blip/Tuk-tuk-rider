package com.example.data.model

import com.example.data.local.entity.CustomerEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderItemEntity
import com.example.data.local.entity.RestaurantEntity

data class OrderDetail(
    val order: OrderEntity,
    val restaurant: RestaurantEntity,
    val customer: CustomerEntity,
    val items: List<OrderItemEntity> = emptyList()
)
