package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.dto.SearchOrderRequest
import com.wutsi.ecommerce.order.dto.SearchOrderResponse
import com.wutsi.ecommerce.order.entity.OrderStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchOrderController.sql"])
class SearchOrdersControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/orders/search"
    }

    @Test
    fun searchByAccount() {
        val request = SearchOrderRequest(
            accountId = ACCOUNT_ID
        )
        val response = rest.postForEntity(url, request, SearchOrderResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val orders = response.body!!.orders
        assertEquals(3, orders.size)
        assertTrue(orders.map { it.id }.containsAll(listOf("110", "111", "120")))
    }

    @Test
    fun searchByMerchant() {
        val request = SearchOrderRequest(
            merchantId = 20L
        )
        val response = rest.postForEntity(url, request, SearchOrderResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val orders = response.body!!.orders
        assertEquals(1, orders.size)
        assertTrue(orders.map { it.id }.containsAll(listOf("120")))
    }

    @Test
    fun search() {
        val request = SearchOrderRequest(
            accountId = 1L,
            status = listOf(OrderStatus.OPENED.name, OrderStatus.DONE.name),
            createdFrom = OffsetDateTime.now().minusDays(90),
            createdTo = OffsetDateTime.now()
        )
        val response = rest.postForEntity(url, request, SearchOrderResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val orders = response.body!!.orders
        assertEquals(1, orders.size)
        assertTrue(orders.map { it.id }.containsAll(listOf("111")))
    }
}
