package com.wutsi.ecommerce.order.entity

import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_ORDER_STATUS")
data class OrderStatusEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_fk")
    val order: OrderEntity = OrderEntity(),

    val previousStatus: OrderStatus? = null,
    val status: OrderStatus = OrderStatus.CREATED,
    val reason: String? = null,
    val comment: String? = null,
    val created: OffsetDateTime = OffsetDateTime.now(),
)
