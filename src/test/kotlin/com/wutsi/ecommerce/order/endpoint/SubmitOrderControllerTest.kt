package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
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
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SubmitOrderController.sql"])
public class SubmitOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: OrderRepository

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun created() {
        val url = "http://localhost:$port/v1/orders/100/submit"
        val response = rest.getForEntity(url, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = dao.findById("100").get()
        assertEquals(OrderStatus.READY, order.status)

        verify(eventStream).publish(
            com.wutsi.ecommerce.order.event.EventURN.ORDER_READY.urn,
            OrderEventPayload("100")
        )
    }

    @Test
    fun ready() {
        val url = "http://localhost:$port/v1/orders/201/submit"
        val response = rest.getForEntity(url, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = dao.findById("201").get()
        assertEquals(OrderStatus.READY, order.status)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun processing() {
        val url = "http://localhost:$port/v1/orders/202/submit"
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)
    }

    @Test
    fun completed() {
        val url = "http://localhost:$port/v1/orders/203/submit"
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)
    }

    @Test
    fun cancelled() {
        val url = "http://localhost:$port/v1/orders/204/submit"
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)
    }

    @Test
    fun notFound() {
        val url = "http://localhost:$port/v1/orders/999/submit"
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)
    }
}
