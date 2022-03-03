package com.wutsi.ecommerce.order.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

public data class Order(
    public val id: String = "",
    public val merchantId: Long = 0,
    public val accountId: Long = 0,
    public val reservationId: Long? = null,
    public val status: String = "",
    public val subTotalPrice: Double = 0.0,
    public val savingsAmount: Double = 0.0,
    public val totalPrice: Double = 0.0,
    public val currency: String = "",
    public val items: List<OrderItem> = emptyList(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val cancelled: OffsetDateTime? = null,
    public val shippingId: Long? = null,
    public val deliveryFees: Double = 0.0,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val expectedDelivered: OffsetDateTime? = null,
    public val shippingAddress: Address? = null
)
