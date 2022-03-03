package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.ecommerce.order.dao.AddressRepository
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetAddressRequest
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
@Sql(value = ["/db/clean.sql", "/db/SetShippingAddressController.sql"])
public class SetShippingAddressControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var orderDao: OrderRepository

    @Autowired
    private lateinit var addressDao: AddressRepository

    @Test
    public fun create() {
        val url = "http://localhost:$port/v1/orders/100/shipping-address"
        val request = SetAddressRequest(
            firstName = "Ray",
            lastName = "Sponsible",
            email = "ray.sponsible@gmail.com",
            cityId = 1111,
            country = "CM",
            street = "This is nice",
            zipCode = "111"
        )
        val response = rest.postForEntity(url, request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val order = orderDao.findById("100").get()
        val address = addressDao.findById(order.shippingAddress?.id).get()
        assertEquals(request.firstName, address.firstName)
        assertEquals(request.lastName, address.lastName)
        assertEquals(request.email, address.email)
        assertEquals(request.cityId, address.cityId)
        assertEquals(request.country, address.country)
        assertEquals(request.street, address.street)
        assertEquals(request.zipCode, address.zipCode)
    }

    @Test
    fun update() {
        val url = "http://localhost:$port/v1/orders/200/shipping-address"
        val request = SetAddressRequest(
            id = 200
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
            id = 300
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
            id = 99999
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.ecommerce.order.error.ErrorURN.ADDRESS_NOT_FOUND.urn, response.error.code)
    }
}
