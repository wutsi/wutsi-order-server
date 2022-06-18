package com.wutsi.ecommerce.order.dto

import kotlin.Double
import kotlin.Int
import kotlin.Long

public data class SetShippingMethodRequest(
    public val shippingId: Long = 0,
    public val deliveryFees: Double = 0.0,
    public val deliveryTime: Int? = null,
)
