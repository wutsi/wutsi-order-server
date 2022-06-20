package com.wutsi.ecommerce.order.dto

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class CreateAddressRequest(
    @get:NotBlank
    public val type: String = "",
    @get:NotBlank
    public val firstName: String = "",
    @get:NotBlank
    public val lastName: String = "",
    public val country: String? = null,
    public val street: String? = null,
    public val cityId: Long? = null,
    public val zipCode: String? = null,
    public val email: String? = null,
)
