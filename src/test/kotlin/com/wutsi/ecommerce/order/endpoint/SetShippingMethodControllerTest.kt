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
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
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
        val url = "http://localhost:$port/v1/orders/100/shipping-method"
        val request = SetShippingMethodRequest(
            shippingId = 111L,
            shippingRate = 10000.0
        )
        val response = rest.postForEntity(url, request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = dao.findById("100").get()
        assertEquals(request.shippingId, order.shippingId)
        assertEquals(request.shippingRate, order.deliveryFees)
    }

    @Test
    public fun completed() {
        val url = "http://localhost:$port/v1/orders/130/shipping-method"
        val request = SetShippingMethodRequest(
            shippingId = 111L,
            shippingRate = 10000.0
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
            shippingRate = 10000.0
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_STATUS.urn, response.error.code)
    }
}
