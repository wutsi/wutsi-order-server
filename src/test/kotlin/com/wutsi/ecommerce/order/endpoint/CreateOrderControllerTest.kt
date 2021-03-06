package com.wutsi.ecommerce.order.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.CreateReservationRequest
import com.wutsi.ecommerce.catalog.dto.CreateReservationResponse
import com.wutsi.ecommerce.catalog.dto.ProductSummary
import com.wutsi.ecommerce.catalog.dto.SearchProductResponse
import com.wutsi.ecommerce.catalog.error.ErrorURN
import com.wutsi.ecommerce.order.dao.OrderItemRepository
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dao.OrderStatusRepository
import com.wutsi.ecommerce.order.dto.CreateOrderItem
import com.wutsi.ecommerce.order.dto.CreateOrderRequest
import com.wutsi.ecommerce.order.dto.CreateOrderResponse
import com.wutsi.ecommerce.order.entity.AddressType
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
class CreateOrderControllerTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var orderDao: OrderRepository

    @Autowired
    private lateinit var itemDao: OrderItemRepository

    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    @Autowired
    private lateinit var statusDao: OrderStatusRepository

    private val request = CreateOrderRequest(
        merchantId = 11L,
        items = listOf(
            CreateOrderItem(
                productId = 11L,
                quantity = 10,
            ),
            CreateOrderItem(
                productId = 12L,
                quantity = 1,
            ),
        ),
        addressType = AddressType.POSTAL.name
    )

    private val products = listOf(
        ProductSummary(id = 11L, price = 100.0, currency = "XAF", comparablePrice = 150.0),
        ProductSummary(id = 12L, price = 200.0, currency = "XAF")
    )

    @Test
    public fun create() {
        // GIVEN
        doReturn(SearchProductResponse(products)).whenever(catalogApi).searchProducts(any())
        doReturn(CreateReservationResponse(555)).whenever(catalogApi).createReservation(any())

        // WHEN
        val url = "http://localhost:$port/v1/orders"
        val response = rest.postForEntity(url, request, CreateOrderResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val order = orderDao.findById(response.body!!.id).get()
        assertEquals(ACCOUNT_ID, order.accountId)
        assertEquals(request.merchantId, order.merchantId)
        assertEquals(555L, order.reservationId)
        assertNotNull(order.created)
        assertEquals(OrderStatus.CREATED, order.status)
        assertEquals(1700.0, order.subTotalPrice)
        assertEquals(1200.0, order.totalPrice)
        assertEquals(500.0, order.savingsAmount)
        assertEquals(order.created.plusMinutes(30), order.expires)
        assertEquals(AddressType.POSTAL, order.addressType)

        val items = itemDao.findByOrder(order)
        assertEquals(2, items.size)
        assertEquals(request.items[0].productId, items[0].productId)
        assertEquals(request.items[0].quantity, items[0].quantity)
        assertEquals(products[0].price, items[0].unitPrice)
        assertEquals(products[0].comparablePrice, items[0].unitComparablePrice)
        assertEquals(products[0].currency, items[0].currency)

        assertEquals(request.items[1].productId, items[1].productId)
        assertEquals(request.items[1].quantity, items[1].quantity)
        assertEquals(products[1].price, items[1].unitPrice)
        assertEquals(products[1].comparablePrice, items[1].unitComparablePrice)
        assertEquals(products[1].currency, items[1].currency)

        val req = argumentCaptor<CreateReservationRequest>()
        verify(catalogApi).createReservation(req.capture())
        assertEquals(order.id, req.firstValue.orderId)
        assertEquals(2, req.firstValue.products.size)
        assertEquals(request.items[0].productId, req.firstValue.products[0].productId)
        assertEquals(request.items[0].quantity, req.firstValue.products[0].quantity)
        assertEquals(request.items[1].productId, req.firstValue.products[1].productId)
        assertEquals(request.items[1].quantity, req.firstValue.products[1].quantity)

        val status = statusDao.findByOrder(order)
        assertEquals(1, status.size)
        assertEquals(OrderStatus.CREATED, status[0].status)
        assertNull(status[0].previousStatus)
    }

    @Test
    public fun availabilityError() {
        // GIVEN
        doReturn(SearchProductResponse(products)).whenever(catalogApi).searchProducts(any())
        doThrow(createFeignException(ErrorURN.OUT_OF_STOCK_ERROR.urn)).whenever(catalogApi).createReservation(any())

        // WHEN
        val url = "http://localhost:$port/v1/orders"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, CreateOrderResponse::class.java)
        }

        // THEN
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.ecommerce.order.error.ErrorURN.PRODUCT_AVAILABILITY_ERROR.urn, response.error.code)
    }

    @Test
    public fun reservationError() {
        // GIVEN
        doReturn(SearchProductResponse(products)).whenever(catalogApi).searchProducts(any())
        doThrow(createFeignException(ErrorURN.ILLEGAL_TENANT_ACCESS.urn)).whenever(catalogApi).createReservation(any())

        // WHEN
        val url = "http://localhost:$port/v1/orders"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, CreateOrderResponse::class.java)
        }

        // THEN
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.ecommerce.order.error.ErrorURN.RESERVATION_ERROR.urn, response.error.code)
    }

    @Test
    public fun missingProduct() {
        // GIVEN
        val products = listOf(
            ProductSummary(id = 12L, price = 200.0, currency = "XAF")
        )
        doReturn(SearchProductResponse(products)).whenever(catalogApi).searchProducts(any())

        // WHEN
        val url = "http://localhost:$port/v1/orders"
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, CreateOrderResponse::class.java)
        }

        // THEN
        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.ecommerce.order.error.ErrorURN.INVALID_PRODUCT_ID.urn, response.error.code)
    }
}
