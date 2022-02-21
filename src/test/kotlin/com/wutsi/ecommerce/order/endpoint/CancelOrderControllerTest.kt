package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.entity.OrderStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    @Autowired
    private lateinit var orderDao: OrderRepository

    @Test
    fun cancel() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/100"
        rest.delete(url)

        // THEN
        val order = orderDao.findById("100").get()
        assertEquals(OrderStatus.CANCELLED, order.status)
        assertNotNull(order.cancelled)
    }

    @Test
    fun alreadyCancelled() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/900"
        rest.delete(url)

        // THEN
        val order = orderDao.findById("900").get()
        assertEquals(true, order.cancelled?.isBefore(OffsetDateTime.now()))
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
    }
}
