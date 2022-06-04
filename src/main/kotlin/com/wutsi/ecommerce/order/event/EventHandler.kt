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
        logger.add("transaction_id", payload.transactionId)
        logger.add("transaction_type", payload.type)
        logger.add("order_id", payload.orderId)

        if (payload.type == TransactionType.CHARGE.name)
            paymentEventHandler.onCharge(payload.orderId)
    }
}
