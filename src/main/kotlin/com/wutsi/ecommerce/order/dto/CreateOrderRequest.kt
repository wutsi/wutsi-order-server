package com.wutsi.ecommerce.order.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import kotlin.Long
import kotlin.collections.List

public data class CreateOrderRequest(
    public val merchantId: Long = 0,
    @get:NotNull
    @get:NotEmpty
    public val items: List<CreateOrderItem> = emptyList()
)
