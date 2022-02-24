package com.wutsi.ecommerce.order.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Double
import kotlin.Long
import kotlin.String

public data class OrderSummary(
    public val id: String = "",
    public val merchantId: Long = 0,
    public val accountId: Long = 0,
    public val reservationId: Long? = null,
    public val status: String = "",
    public val subTotalPrice: Double = 0.0,
    public val deliveryFees: Double = 0.0,
    public val savingsAmount: Double = 0.0,
    public val totalPrice: Double = 0.0,
    public val currency: String = "",
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val cancelled: OffsetDateTime? = null
)
