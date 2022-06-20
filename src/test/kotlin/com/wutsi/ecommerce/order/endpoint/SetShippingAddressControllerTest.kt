package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetAddressRequest
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SetShippingAddressController.sql"])
public class SetShippingAddressControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var orderDao: OrderRepository

    @Test
    fun update() {
        val url = "http://localhost:$port/v1/orders/200/shipping-address"
        val request = SetAddressRequest(
            addressId = 200
        )
        val response = rest.postForEntity(url, request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = orderDao.findById("200").get()
        assertEquals(200, order.shippingAddress?.id)
    }

    @Test
    fun illegalAccess() {
        val url = "http://localhost:$port/v1/orders/200/shipping-address"
        val request = SetAddressRequest(
            addressId = 300
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.ecommerce.order.error.ErrorURN.ILLEGAL_ADDRESS_ACCESS.urn, response.error.code)
    }

    @Test
    fun notFound() {
        val url = "http://localhost:$port/v1/orders/200/shipping-address"
        val request = SetAddressRequest(
            addressId = 99999
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.ecommerce.order.error.ErrorURN.ADDRESS_NOT_FOUND.urn, response.error.code)
    }
}
