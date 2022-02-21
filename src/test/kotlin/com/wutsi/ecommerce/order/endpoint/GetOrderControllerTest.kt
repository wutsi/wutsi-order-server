package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dto.GetOrderResponse
import com.wutsi.ecommerce.order.entity.OrderStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetOrderController.sql"])
class GetOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun invoke() {
        val response = rest.getForEntity(url(1), GetOrderResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = response.body!!.order
        assertEquals(900.0, order.totalPrice)
        assertEquals("XAF", order.currency)
        assertEquals(11L, order.merchantId)
        assertEquals(1L, order.accountId)
        assertEquals(OrderStatus.CREATED.name, order.status)

        assertEquals(2, order.items.size)
        assertEquals(4, order.items[0].quantity)
        assertEquals(100.0, order.items[0].unitPrice)
        assertEquals(11, order.items[0].productId)

        assertEquals(2, order.items[1].quantity)
        assertEquals(250.0, order.items[1].unitPrice)
        assertEquals(12, order.items[1].productId)
    }

    @Test
    fun notFound() {
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(999), GetOrderResponse::class.java)
        }

        assertEquals(404, ex.rawStatusCode)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/orders/$id"
}
