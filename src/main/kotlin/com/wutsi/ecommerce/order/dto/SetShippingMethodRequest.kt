package com.wutsi.ecommerce.order.dto

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class SetShippingMethodRequest(
    public val shippingId: Long = 0,
    @get:NotBlank
    public val country: String = "",
    public val cityId: Long? = null
)
