package com.wutsi.ecommerce.order.dto

import kotlin.Long
import kotlin.String

public data class SetAddressRequest(
    public val id: Long? = null,
    public val firstName: String = "",
    public val lastName: String = "",
    public val country: String = "",
    public val street: String? = null,
    public val cityId: Long? = null,
    public val zipCode: String? = null,
    public val email: String? = null
)
