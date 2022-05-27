package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingOrderRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SetShippingOrderController.sql"])
class SetShippingOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: OrderRepository

    @Test
    fun invoke() {
        val url = "http://localhost:$port/v1/orders/100/shipping-order"
        val request = SetShippingOrderRequest(
            shippingOrderId = 999L,
        )
        val response = rest.postForEntity(url, request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = dao.findById("100").get()
        assertEquals(request.shippingOrderId, order.shippingOrderId)
    }
}
