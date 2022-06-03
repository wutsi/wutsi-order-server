package com.wutsi.ecommerce.order.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.payment.entity.TransactionType
import com.wutsi.platform.payment.event.EventURN
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val paymentEventHandler: PaymentEventHandler,
    private val logger: KVLogger,
) {
    @EventListener
    fun onEvent(event: Event) {
        if (EventURN.TRANSACTION_SUCCESSFUL.urn == event.type) {
            val payload = objectMapper.readValue(event.payload, TransactionEventPayload::class.java)
            onTransactionSuccessful(payload)
        }
    }

    private fun onTransactionSuccessful(payload: TransactionEventPayload) {
        if (payload.type != TransactionType.CHARGE.name)
            return

        logger.add("transaction_id", payload.transactionId)
        logger.add("order_id", payload.orderId)

        if (payload.orderId != null)
            paymentEventHandler.changeReceived(payload.orderId!!)
    }
}
