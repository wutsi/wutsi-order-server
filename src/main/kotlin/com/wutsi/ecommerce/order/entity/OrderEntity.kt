package com.wutsi.ecommerce.order.entity

import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "T_ORDER")
data class OrderEntity(
    @Id
    val id: String? = null,

    val tenantId: Long = -1,
    val merchantId: Long = -1,
    val accountId: Long = -1,
    var reservationId: Long? = null,

    var status: OrderStatus = OrderStatus.CREATED,
    val totalPrice: Double = 0.0,
    val subTotalPrice: Double = 0.0,
    val savingsAmount: Double = 0.0,
    val deliveryFees: Double = 0.0,
    val currency: String = "",

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    val items: MutableList<OrderItemEntity> = mutableListOf(),

    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now(),
    var cancelled: OffsetDateTime? = null
)
