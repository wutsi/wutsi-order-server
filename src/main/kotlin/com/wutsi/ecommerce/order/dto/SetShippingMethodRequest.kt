package com.wutsi.ecommerce.order.dto

import kotlin.Double
import kotlin.Long

public data class SetShippingMethodRequest(
    public val shippingId: Long = 0,
    public val shippingRate: Double = 0.0
)
