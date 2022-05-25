package com.wutsi.ecommerce.order.entity

import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
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
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    var totalPrice: Double = 0.0,
    var totalPaid: Double = 0.0,
    val subTotalPrice: Double = 0.0,
    val savingsAmount: Double = 0.0,
    var deliveryFees: Double = 0.0,
    val currency: String = "",

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    var items: List<OrderItemEntity> = emptyList(),

    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now(),
    var expectedDelivered: OffsetDateTime? = null,
    var shippingId: Long? = null,
    val expires: OffsetDateTime = OffsetDateTime.now().plusHours(30),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_fk")
    var shippingAddress: AddressEntity? = null,
) {
    fun updateTotalPrice() {
        totalPrice = subTotalPrice + deliveryFees - savingsAmount
        if (totalPrice <= 0.0)
            paymentStatus = PaymentStatus.PAID
    }

    fun ensureNotClosed() {
        if (status == OrderStatus.DONE || status == OrderStatus.CANCELLED)
            throw ConflictException(
                error = Error(
                    code = ErrorURN.ILLEGAL_STATUS.urn,
                    data = mapOf(
                        "status" to status
                    )
                )
            )
    }
}
