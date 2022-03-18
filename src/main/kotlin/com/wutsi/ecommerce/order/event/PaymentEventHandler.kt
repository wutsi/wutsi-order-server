package com.wutsi.ecommerce.order.event

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.entity.PaymentStatus
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.SearchTransactionRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PaymentEventHandler(
    private val paymentApi: WutsiPaymentApi,
    private val dao: OrderRepository,
    private val logger: KVLogger,
    private val eventStream: EventStream
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentEventHandler::class.java)
    }

    /**
     * On payment Received, update the status of the order
     */
    @Transactional
    fun paymentReceived(orderId: String) {
        val order = dao.findById(orderId).orElse(null)
            ?: return

        var fireEvent = false
        val txs = paymentApi.searchTransaction(
            request = SearchTransactionRequest(
                orderId = order.id,
                limit = 1000
            )
        ).transactions
        if (txs.isNotEmpty()) {
            val total = txs.sumOf { it.amount }

            order.totalPaid = total
            order.paymentStatus = if (total >= order.totalPrice) PaymentStatus.PAID else PaymentStatus.PARTIALLY_PAID
            if (order.status == OrderStatus.CREATED && order.paymentStatus == PaymentStatus.PAID) {
                order.status = OrderStatus.READY
                fireEvent = true
            }
            dao.save(order)
        }

        // Log
        logger.add("payments_received_count", txs.size)
        logger.add("payment_status", order.paymentStatus)
        logger.add("order_status", order.status)

        // Fire event
        if (fireEvent)
            publish(EventURN.ORDER_READY, order)
    }

    private fun publish(type: EventURN, order: OrderEntity) {
        try {
            eventStream.publish(type.urn, OrderEventPayload(order.id!!))
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push event", ex)
        }
    }
}
