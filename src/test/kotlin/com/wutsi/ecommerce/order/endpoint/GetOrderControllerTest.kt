package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dto.GetOrderResponse
import com.wutsi.ecommerce.order.entity.OrderStatus
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetOrderController.sql"])
class GetOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun invoke() {
        val response = rest.getForEntity(url(100), GetOrderResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = response.body!!.order
        assertEquals(777L, order.reservationId)
        assertEquals(1150.0, order.totalPrice)
        assertEquals(900.0, order.subTotalPrice)
        assertEquals(150.0, order.deliveryFees)
        assertEquals(100.0, order.savingsAmount)
        assertEquals("XAF", order.currency)
        assertEquals(11L, order.merchantId)
        assertEquals(1L, order.accountId)
        assertEquals(OrderStatus.CREATED.name, order.status)
        assertNull(order.cancelled)
        assertEquals(333L, order.shippingId)

        assertEquals(2, order.items.size)
        assertEquals(4, order.items[0].quantity)
        assertEquals(11L, order.items[0].productId)
        assertEquals(100.0, order.items[0].unitPrice)
        assertNull(order.items[0].unitComparablePrice)

        assertEquals(2, order.items[1].quantity)
        assertEquals(12L, order.items[1].productId)
        assertEquals(250.0, order.items[1].unitPrice)
        assertEquals(300.0, order.items[1].unitComparablePrice)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/orders/$id"
}
