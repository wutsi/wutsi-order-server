package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dao.OrderStatusRepository
import com.wutsi.ecommerce.order.dto.ChangeStatusRequest
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ChangeStatusController.sql"])
class ChangeStatusControllerDoneTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var eventStream: EventStream

    @Autowired
    private lateinit var orderDao: OrderRepository

    @Autowired
    private lateinit var statusDao: OrderStatusRepository

    @Test
    fun created() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/100/status"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, ChangeStatusRequest(status = OrderStatus.DONE.name), Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun opened() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/101/status"
        rest.postForEntity(
            url,
            ChangeStatusRequest(status = OrderStatus.DONE.name),
            Any::class.java
        )

        // THEN
        val order = orderDao.findById("101").get()
        assertEquals(OrderStatus.DONE, order.status)

        verify(eventStream).publish(
            com.wutsi.ecommerce.order.event.EventURN.ORDER_DONE.urn,
            OrderEventPayload("101")
        )

        val statuses = statusDao.findByOrder(order)
        assertEquals(1, statuses.size)
        assertEquals("101", statuses[0].order.id)
        assertEquals(OrderStatus.DONE, statuses[0].status)
        assertEquals(OrderStatus.OPENED, statuses[0].previousStatus)
        assertNotNull(statuses[0].created)
        assertNull(statuses[0].reason)
        assertNull(statuses[0].comment)
    }

    @Test
    fun done() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/102/status"
        val response = rest.postForEntity(url, ChangeStatusRequest(OrderStatus.DONE.name), Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = orderDao.findById("102").get()
        assertEquals(OrderStatus.DONE, order.status)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun cancelled() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/103/status"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, ChangeStatusRequest(status = OrderStatus.DONE.name), Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun expired() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/104/status"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, ChangeStatusRequest(status = OrderStatus.DONE.name), Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }
}
