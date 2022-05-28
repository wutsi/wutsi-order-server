package com.wutsi.ecommerce.order.entity

enum class OrderStatus {
    CREATED,
    OPENED,
    DONE,
    CANCELLED,
    EXPIRED,
    READY_FOR_PICKUP,
    IN_TRANSIT,
    DELIVERED,
}
