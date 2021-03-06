package com.wutsi.ecommerce.order.delegate

import com.wutsi.ecommerce.order.dto.Address
import com.wutsi.ecommerce.order.dto.Order
import com.wutsi.ecommerce.order.dto.OrderItem
import com.wutsi.ecommerce.order.dto.OrderSummary
import com.wutsi.ecommerce.order.entity.AddressEntity
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderItemEntity

fun OrderItemEntity.toOrderItem() = OrderItem(
    productId = this.productId,
    currency = this.currency,
    unitPrice = this.unitPrice,
    unitComparablePrice = if (this.unitComparablePrice != null && this.unitComparablePrice > this.unitPrice)
        this.unitComparablePrice
    else
        null,
    quantity = this.quantity,
)

fun OrderEntity.toOrder() = Order(
    id = this.id ?: "",
    tenantId = this.tenantId,
    merchantId = this.merchantId,
    accountId = this.accountId,
    status = this.status.name,
    created = this.created,
    updated = this.updated,
    items = this.items.map { it.toOrderItem() },
    currency = this.currency,
    totalPrice = this.totalPrice,
    reservationId = this.reservationId,
    subTotalPrice = this.subTotalPrice,
    savingsAmount = this.savingsAmount,
    deliveryFees = this.deliveryFees,
    shippingId = this.shippingId,
    expectedDelivered = this.expectedDelivered,
    shippingAddress = this.shippingAddress?.toAddress(),
    paymentStatus = this.paymentStatus.name,
    totalPaid = this.totalPaid,
    addressType = this.addressType.name,
)

fun OrderEntity.toOrderSummary() = OrderSummary(
    id = this.id ?: "",
    merchantId = this.merchantId,
    accountId = this.accountId,
    status = this.status.name,
    created = this.created,
    updated = this.updated,
    currency = this.currency,
    totalPrice = this.totalPrice,
    reservationId = this.reservationId,
    subTotalPrice = this.subTotalPrice,
    savingsAmount = this.savingsAmount,
    deliveryFees = this.deliveryFees,
    paymentStatus = this.paymentStatus.name,
    totalPaid = this.totalPaid,
)

fun AddressEntity.toAddress() = Address(
    id = this.id ?: -1,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    cityId = this.cityId,
    country = this.country,
    street = this.street,
    zipCode = this.zipCode,
    type = this.type.name
)
