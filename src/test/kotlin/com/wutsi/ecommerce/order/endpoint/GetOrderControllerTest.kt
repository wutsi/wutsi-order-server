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
        assertEquals(900.0, order.totalPrice)
        assertEquals("XAF", order.currency)
        assertEquals(11L, order.merchantId)
        assertEquals(1L, order.accountId)
        assertEquals(OrderStatus.CREATED.name, order.status)
        assertNull(order.cancelled)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/orders/$id"
}
