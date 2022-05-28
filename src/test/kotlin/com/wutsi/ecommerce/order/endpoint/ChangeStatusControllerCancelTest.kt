package com.wutsi.ecommerce.order.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.order.dto.ChangeStatusRequest
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.event.EventURN
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

class ChangeStatusControllerCancelTest : AbstractChangeStatusControllerTest() {
    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    private val status = OrderStatus.CANCELLED
    private val event = EventURN.ORDER_CANCELLED

    @Test
    fun created() {
        changeStatusSuccess("100", OrderStatus.CREATED, status, event)
        verify(catalogApi, never()).cancelReservation(any())
    }

    @Test
    fun opened() {
        changeStatusSuccess("101", OrderStatus.OPENED, status, event)
        verify(catalogApi).cancelReservation(1001)
    }

    @Test
    fun done() {
        changeStatusSuccess("102", OrderStatus.DONE, status, event)
        verify(catalogApi).cancelReservation(1002)
    }

    @Test
    fun cancelled() {
        changeStatusSuccess("103", OrderStatus.CANCELLED, status, event)
        verify(catalogApi, never()).cancelReservation(any())
    }

    @Test
    fun expired() {
        changeStatusSuccessBadStatus("104", status)
        verify(catalogApi, never()).cancelReservation(any())
    }

    @Test
    fun readyForPickup() {
        // WHEN
        changeStatusSuccess("105", OrderStatus.READY_FOR_PICKUP, status, event)
        verify(catalogApi).cancelReservation(1005)
    }

    @Test
    fun inTransit() {
        // WHEN
        changeStatusSuccess("106", OrderStatus.IN_TRANSIT, status, event)
        verify(catalogApi).cancelReservation(1006)
    }

    @Test
    fun delivered() {
        // WHEN
        changeStatusSuccessBadStatus("107", status)
        verify(catalogApi, never()).cancelReservation(any())
    }

    @Test
    fun notFound() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/9999/status"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, ChangeStatusRequest(status = OrderStatus.CANCELLED.name), Any::class.java)
        }

        // THEN
        assertEquals(404, ex.rawStatusCode)

        verify(eventStream, never()).publish(any(), any())
        verify(catalogApi, never()).cancelReservation(any())
    }
}
