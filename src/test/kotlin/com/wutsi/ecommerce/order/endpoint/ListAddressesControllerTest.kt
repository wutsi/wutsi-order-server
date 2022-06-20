package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dto.ListAddressResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ListAddressController.sql"])
public class ListAddressesControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun all() {
        val url = "http://localhost:$port/v1/addresses"
        val response = rest.getForEntity(url, ListAddressResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val addresses = response.body!!.addresses
        assertEquals(2, addresses.size)
    }

    @Test
    public fun email() {
        val url = "http://localhost:$port/v1/addresses?type=EMAIL"
        val response = rest.getForEntity(url, ListAddressResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val addresses = response.body!!.addresses
        assertEquals(1, addresses.size)
    }
}
