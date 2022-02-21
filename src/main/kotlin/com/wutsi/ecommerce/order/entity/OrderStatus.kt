package com.wutsi.ecommerce.order.entity

enum class OrderStatus {
    CREATED,
    PROCESSING,
    UNDER_DELIVERY,
    DELIVERED,
    COMPLETED,
    CANCELLED,
}
