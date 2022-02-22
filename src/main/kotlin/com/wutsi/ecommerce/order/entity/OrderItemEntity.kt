package com.wutsi.ecommerce.order.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_ORDER_ITEM")
data class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val productId: Long = -1,

    val unitPrice: Double = 0.0,
    val unitComparablePrice: Double? = null,
    val currency: String = "",
    val quantity: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_fk")
    val order: OrderEntity = OrderEntity()
)
