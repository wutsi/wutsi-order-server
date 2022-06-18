package com.wutsi.ecommerce.order.dto

import kotlin.collections.List

public data class ListAddressResponse(
    public val addresses: List<Address> = emptyList(),
)
