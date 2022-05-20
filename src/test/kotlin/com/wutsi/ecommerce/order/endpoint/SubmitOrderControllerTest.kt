package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.analytics.tracking.WutsiTrackingApi
import com.wutsi.analytics.tracking.dto.PushTrackRequest
import com.wutsi.analytics.tracking.entity.EventType
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SubmitOrderController.sql"])
class SubmitOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: OrderRepository

    @MockBean
    private lateinit var eventStream: EventStream

    @MockBean
    private lateinit var trackingApi: WutsiTrackingApi

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

        val request = argumentCaptor<PushTrackRequest>()
        verify(trackingApi, times(2)).push(request.capture())

        val track1 = request.firstValue.track
        assertEquals(ACCOUNT_ID.toString(), track1.accountId)
        assertEquals(order.merchantId.toString(), track1.merchantId)
        assertEquals(TENANT_ID, track1.tenantId)
        assertEquals(DEVICE_ID, track1.deviceId)
        assertNotNull(track1.correlationId)
        assertEquals("11", track1.productId)
        assertNull(track1.page)
        assertEquals(EventType.ORDER.name, track1.event)
        assertNull(track1.impressions)
        assertNull(track1.lat)
        assertNull(track1.long)
        assertNull(track1.url)
        assertEquals(400.0, track1.value)

        val track2 = request.secondValue.track
        assertEquals(ACCOUNT_ID.toString(), track2.accountId)
        assertEquals(order.merchantId.toString(), track2.merchantId)
        assertEquals(TENANT_ID, track2.tenantId)
        assertEquals(DEVICE_ID, track2.deviceId)
        assertEquals(track1.correlationId, track2.correlationId)
        assertEquals("12", track2.productId)
        assertNull(track1.page)
        assertEquals(EventType.ORDER.name, track2.event)
        assertNull(track2.url)
        assertNull(track2.impressions)
        assertNull(track2.lat)
        assertNull(track2.long)
        assertEquals(500.0, track2.value)
    }

    @Test
    fun ready() {
        val url = "http://localhost:$port/v1/orders/201/submit"
        val response = rest.getForEntity(url, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = dao.findById("201").get()
        assertEquals(OrderStatus.READY, order.status)

        verify(eventStream, never()).publish(any(), any())
        verify(trackingApi, never()).push(any())
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

        verify(eventStream, never()).publish(any(), any())
        verify(trackingApi, never()).push(any())
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

        verify(eventStream, never()).publish(any(), any())
        verify(trackingApi, never()).push(any())
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

        verify(eventStream, never()).publish(any(), any())
        verify(trackingApi, never()).push(any())
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
