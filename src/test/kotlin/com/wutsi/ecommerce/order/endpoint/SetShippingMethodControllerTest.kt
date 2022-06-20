package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingMethodRequest
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SetShippingMethodController.sql"])
public class SetShippingMethodControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: OrderRepository

    @Test
    public fun invoke() {
        // WHEN
        val url = "http://localhost:$port/v1/orders/100/shipping-method"
        val request = SetShippingMethodRequest(
            shippingId = 111L,
            deliveryTime = 24,
            deliveryFees = 200.0
        )
        val response = rest.postForEntity(url, request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val delivered = OffsetDateTime.now().plusDays(1)
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val order = dao.findById("100").get()
        assertEquals(request.shippingId, order.shippingId)
        assertEquals(fmt.format(delivered), fmt.format(order.expectedDelivered))
        assertEquals(request.deliveryFees, order.deliveryFees)
        assertEquals(1000.0, order.totalPrice)
    }

    @Test
    public fun expired() {
        val url = "http://localhost:$port/v1/orders/130/shipping-method"
        val request = SetShippingMethodRequest(
            shippingId = 111L,
            deliveryTime = 24,
            deliveryFees = 200.0
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)
    }

    @Test
    public fun cancelled() {
        val url = "http://localhost:$port/v1/orders/140/shipping-method"
        val request = SetShippingMethodRequest(
            shippingId = 111L,
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)
    }
}
