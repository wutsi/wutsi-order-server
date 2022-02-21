package com.wutsi.ecommerce.order.delegate

import com.wutsi.ecommerce.order.dto.Order
import com.wutsi.ecommerce.order.dto.OrderItem
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderItemEntity

fun OrderItemEntity.toOrderItem() = OrderItem(
    productId = this.productId,
    currency = this.currency,
    unitPrice = this.unitPrice,
    quantity = this.quantity,
)

fun OrderEntity.toOrder() = Order(
    id = this.id ?: "",
    merchantId = this.merchantId,
    accountId = this.accountId,
    status = this.status.name,
    created = this.created,
    updated = this.updated,
    items = this.items.map { it.toOrderItem() },
    currency = this.currency,
    totalPrice = this.totalPrice,
    cancelled = this.cancelled,
    reservationId = this.reservationId
)
