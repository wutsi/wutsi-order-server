package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dao.AddressRepository
import com.wutsi.ecommerce.order.dto.CreateAddressRequest
import com.wutsi.ecommerce.order.dto.CreateAddressResponse
import com.wutsi.ecommerce.order.entity.AddressType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
public class CreateAddressControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var addressDao: AddressRepository

    @Test
    public fun createPostalAddress() {
        val url = "http://localhost:$port/v1/addresses"
        val request = CreateAddressRequest(
            firstName = "Ray",
            lastName = "Sponsible",
            cityId = 1111,
            country = "CM",
            street = "This is nice",
            zipCode = "111",
            type = AddressType.POSTAL.name
        )
        val response = rest.postForEntity(url, request, CreateAddressResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val address = addressDao.findById(response.body!!.id).get()
        assertEquals(request.firstName, address.firstName)
        assertEquals(request.lastName, address.lastName)
        assertNull(address.email)
        assertEquals(request.cityId, address.cityId)
        assertEquals(request.country, address.country)
        assertEquals(request.street, address.street)
        assertEquals(request.zipCode, address.zipCode)
        assertEquals(AddressType.POSTAL, address.type)
    }

    @Test
    public fun createEmailAddress() {
        val url = "http://localhost:$port/v1/addresses"
        val request = CreateAddressRequest(
            firstName = "Ray",
            lastName = "Sponsible",
            email = "ray.sponsible@gmail.com",
            type = AddressType.EMAIL.name
        )
        val response = rest.postForEntity(url, request, CreateAddressResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val address = addressDao.findById(response.body!!.id).get()
        assertEquals(request.firstName, address.firstName)
        assertEquals(request.lastName, address.lastName)
        assertEquals(request.email, address.email)
        assertNull(address.cityId)
        assertNull(address.country)
        assertNull(address.street)
        assertNull(address.zipCode)
        assertEquals(AddressType.EMAIL, address.type)
    }
}
