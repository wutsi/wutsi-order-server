package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.ProductSummary
import com.wutsi.ecommerce.catalog.dto.SearchProductResponse
import com.wutsi.ecommerce.catalog.entity.ProductType
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingMethodRequest
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.shipping.WutsiShippingApi
import com.wutsi.ecommerce.shipping.dto.RateSummary
import com.wutsi.ecommerce.shipping.dto.SearchRateResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
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

    @MockBean
    private lateinit var shippingApi: WutsiShippingApi

    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    @Test
    public fun invoke() {
        // GIVEN
        val products = listOf(
            ProductSummary(id = 11, type = ProductType.PHYSICAL.name),
            ProductSummary(id = 12, type = ProductType.PHYSICAL.name)
        )
        doReturn(SearchProductResponse(products)).whenever(catalogApi).searchProducts(any())

        val rate = RateSummary(shippingId = 111L, rate = 200.0, deliveryTime = 24)
        doReturn(SearchRateResponse(listOf(rate))).whenever(shippingApi).searchRate(any())

        // WHEN
        val url = "http://localhost:$port/v1/orders/100/shipping-method"
        val request = SetShippingMethodRequest(
            shippingId = 111L,
        )
        val response = rest.postForEntity(url, request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val delivered = OffsetDateTime.now().plusDays(1)
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val order = dao.findById("100").get()
        assertEquals(request.shippingId, order.shippingId)
        assertEquals(fmt.format(delivered), fmt.format(order.expectedDelivered))
        assertEquals(rate.rate, order.deliveryFees)
        assertEquals(1000.0, order.totalPrice)
    }

    @Test
    public fun completed() {
        val url = "http://localhost:$port/v1/orders/130/shipping-method"
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
