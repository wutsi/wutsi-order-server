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
import com.wutsi.ecommerce.order.event.EventURN
import com.wutsi.ecommerce.order.event.OrderEventPayload
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ChangeStatusController.sql"])
abstract class AbstractChangeStatusControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    protected lateinit var eventStream: EventStream

    @Autowired
    protected lateinit var orderDao: OrderRepository

    @Autowired
    protected lateinit var statusDao: OrderStatusRepository

    protected fun changeStatusSuccess(
        orderId: String,
        fromStatus: OrderStatus,
        toStatus: OrderStatus,
        event: EventURN?
    ) {
        // WHEN
        val url = "http://localhost:$port/v1/orders/$orderId/status"
        val request = ChangeStatusRequest(
            status = toStatus.name,
            "no_inventory",
            "None of the product is available"
        )
        rest.postForEntity(url, request, Any::class.java)

        // THEN
        val order = orderDao.findById(orderId).get()
        assertEquals(toStatus, order.status)

        if (fromStatus != toStatus) {
            if (event != null)
                verify(eventStream).publish(event.urn, OrderEventPayload(orderId))
            else
                verify(eventStream, never()).publish(any(), any())

            val statuses = statusDao.findByOrder(order)
            assertEquals(1, statuses.size)
            assertEquals(orderId, statuses[0].order.id)
            assertEquals(toStatus, statuses[0].status)
            assertEquals(fromStatus, statuses[0].previousStatus)
            assertNotNull(statuses[0].created)
            assertEquals(request.reason, statuses[0].reason)
            assertEquals(request.comment, statuses[0].comment)
        } else {
            verify(eventStream, never()).publish(any(), any())

            val statuses = statusDao.findByOrder(order)
            assertTrue(statuses.isEmpty())
        }
    }

    protected fun changeStatusSuccessBadStatus(orderId: String, status: OrderStatus) {
        // WHEN
        val url = "http://localhost:$port/v1/orders/$orderId/status"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, ChangeStatusRequest(status = status.name), Any::class.java)
        }

        // THEN
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }
}
