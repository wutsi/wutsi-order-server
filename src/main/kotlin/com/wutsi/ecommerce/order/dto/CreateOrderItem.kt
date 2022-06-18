package com.wutsi.ecommerce.order.dto

import javax.validation.constraints.Min
import kotlin.Int
import kotlin.Long

public data class CreateOrderItem(
    public val productId: Long = 0,
    @get:Min(1)
    public val quantity: Int = 0,
)
