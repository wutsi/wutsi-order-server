package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.order.event.OrderEventPayload
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CancelOrderController.sql"])
class CancelOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var eventStream: EventStream

    @Autowired
    private lateinit var orderDao: OrderRepository

    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    @Test
    fun cancel() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/100"
        rest.delete(url)

        // THEN
        val order = orderDao.findById("100").get()
        assertEquals(OrderStatus.CANCELLED, order.status)
        assertNotNull(order.cancelled)

        verify(eventStream).publish(
            com.wutsi.ecommerce.order.event.EventURN.ORDER_CANCELLED.urn,
            OrderEventPayload("100")
        )

        verify(catalogApi).cancelReservation(1001)
    }

    @Test
    fun processed() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/203"
        val ex = assertThrows<HttpClientErrorException> {
            rest.delete(url)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
        verify(catalogApi, never()).cancelReservation(any())
    }

    @Test
    fun cancelled() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/204"
        rest.delete(url)

        // THEN
        val order = orderDao.findById("204").get()
        assertEquals(true, order.cancelled?.isBefore(OffsetDateTime.now()))

        verify(eventStream, never()).publish(any(), any())
        verify(catalogApi, never()).cancelReservation(any())
    }

    @Test
    fun notFound() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/9999"
        val ex = assertThrows<HttpClientErrorException> {
            rest.delete(url)
        }

        // THEN
        assertEquals(404, ex.rawStatusCode)

        verify(eventStream, never()).publish(any(), any())
        verify(catalogApi, never()).cancelReservation(any())
    }
}
