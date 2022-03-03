package com.wutsi.ecommerce.order.dto

public data class OrderItem(
    public val productId: Long = 0,
    public val quantity: Int = 0,
    public val unitPrice: Double = 0.0,
    public val unitComparablePrice: Double? = null,
    public val currency: String = ""
)
