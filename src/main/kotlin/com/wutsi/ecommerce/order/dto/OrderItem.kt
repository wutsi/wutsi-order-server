package com.wutsi.ecommerce.order.dto

import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class OrderItem(
    public val productId: Long = 0,
    public val quantity: Int = 0,
    public val unitPrice: Double = 0.0,
    public val currency: String = ""
)
