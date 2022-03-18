package com.wutsi.ecommerce.order.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.endpoint.AbstractEndpointTest
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.entity.PaymentStatus
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.stream.EventTracingData
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.SearchTransactionResponse
import com.wutsi.platform.payment.dto.TransactionSummary
import com.wutsi.platform.payment.entity.TransactionType
import com.wutsi.platform.payment.event.EventURN
import com.wutsi.platform.payment.event.TransactionEventPayload
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.time.OffsetDateTime
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
internal class EventHandlerTest : AbstractEndpointTest() {
    @Autowired
    private lateinit var handler: EventHandler

    @Autowired
    private lateinit var dao: OrderRepository

    @MockBean
    private lateinit var paymentApi: WutsiPaymentApi

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun paid() {
        // GIVEN
        val orderId = "100"
        val txs = listOf(
            TransactionSummary(amount = 1050.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId)
        handler.onEvent(event)

        // THEN
        val order = dao.findById(orderId).get()
        order.status = OrderStatus.READY
        order.paymentStatus = PaymentStatus.PAID
        order.totalPaid = 1050.0

        verify(eventStream).publish(
            com.wutsi.ecommerce.order.event.EventURN.ORDER_READY.urn,
            OrderEventPayload(orderId)
        )
    }

    @Test
    fun paidMultiple() {
        // GIVEN
        val orderId = "101"
        val txs = listOf(
            TransactionSummary(amount = 1000.0),
            TransactionSummary(amount = 50.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId)
        handler.onEvent(event)

        // THEN
        val order = dao.findById(orderId).get()
        order.status = OrderStatus.READY
        order.paymentStatus = PaymentStatus.PAID
        order.totalPaid = 1050.0

        verify(eventStream).publish(
            com.wutsi.ecommerce.order.event.EventURN.ORDER_READY.urn,
            OrderEventPayload(orderId)
        )
    }

    @Test
    fun partial() {
        // GIVEN
        val orderId = "102"
        val txs = listOf(
            TransactionSummary(amount = 100.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId)
        handler.onEvent(event)

        // THEN
        val order = dao.findById(orderId).get()
        order.status = OrderStatus.CREATED
        order.paymentStatus = PaymentStatus.PARTIALLY_PAID
        order.totalPaid = 100.0

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun cashin() {
        // GIVEN
        val orderId = "200"
        val txs = listOf(
            TransactionSummary(amount = 1050.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId, TransactionType.CASHIN)
        handler.onEvent(event)

        // THEN
        verify(eventStream, never()).publish(any(), any())

        val order = dao.findById(orderId).get()
        order.status = OrderStatus.CREATED
        order.paymentStatus = PaymentStatus.PENDING
    }

    @Test
    fun cashout() {
        // GIVEN
        val orderId = "200"
        val txs = listOf(
            TransactionSummary(amount = 1050.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId, TransactionType.CASHOUT)
        handler.onEvent(event)

        // THEN
        verify(eventStream, never()).publish(any(), any())

        val order = dao.findById(orderId).get()
        order.status = OrderStatus.CREATED
        order.paymentStatus = PaymentStatus.PENDING
    }

    @Test
    fun payment() {
        // GIVEN
        val orderId = "200"
        val txs = listOf(
            TransactionSummary(amount = 1050.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId, TransactionType.PAYMENT)
        handler.onEvent(event)

        // THEN
        verify(eventStream, never()).publish(any(), any())

        val order = dao.findById(orderId).get()
        order.status = OrderStatus.CREATED
        order.paymentStatus = PaymentStatus.PENDING
    }

    @Test
    fun badOrderId() {
        // GIVEN
        val orderId = "9999"
        val txs = listOf(
            TransactionSummary(amount = 1050.0),
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        // WHEN
        val event = createEvent(EventURN.TRANSACTION_SUCCESSFUL.urn, orderId)
        handler.onEvent(event)

        // THEN
        verify(eventStream, never()).publish(any(), any())
    }

    private fun createEvent(
        eventType: String,
        orderId: String,
        transactionType: TransactionType = TransactionType.TRANSFER
    ): Event =
        Event(
            id = UUID.randomUUID().toString(),
            type = eventType,
            timestamp = OffsetDateTime.now(),
            tracingData = EventTracingData(
                tenantId = TENANT_ID.toString(),
                traceId = UUID.randomUUID().toString(),
                clientId = "-",
                clientInfo = "--",
                deviceId = DEVICE_ID
            ),
            payload = ObjectMapper().writeValueAsString(
                TransactionEventPayload(
                    orderId = orderId,
                    type = transactionType.name
                )
            )
        )
}
