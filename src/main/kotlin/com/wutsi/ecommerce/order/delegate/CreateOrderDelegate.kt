package com.wutsi.ecommerce.order.`delegate`

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.CreateReservationRequest
import com.wutsi.ecommerce.catalog.dto.ProductSummary
import com.wutsi.ecommerce.catalog.dto.ReservationProduct
import com.wutsi.ecommerce.catalog.dto.SearchProductRequest
import com.wutsi.ecommerce.order.dao.OrderItemRepository
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.CreateOrderRequest
import com.wutsi.ecommerce.order.dto.CreateOrderResponse
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderItemEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.order.service.SecurityManager
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import feign.FeignException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID
import javax.transaction.Transactional
import kotlin.math.max

@Service
class CreateOrderDelegate(
    private val catalogApi: WutsiCatalogApi,
    private val orderDao: OrderRepository,
    private val itemDao: OrderItemRepository,
    private val securityManager: SecurityManager,
    private val objectMapper: ObjectMapper,
    private val logger: KVLogger,
) {
    @Transactional
    fun invoke(request: CreateOrderRequest): CreateOrderResponse {
        val order = createOrder(request)
        logger.add("order_id", order.id)

        order.reservationId = createReservation(order)
        logger.add("reservation_id", order.reservationId)
        orderDao.save(order)

        return CreateOrderResponse(id = order.id!!)
    }

    private fun createOrder(request: CreateOrderRequest): OrderEntity {
        // Products
        val products = findProducts(request)
        val productMap = products.map { it.id to it }.toMap()
        val subTotal = computeSubTotalPrice(request, productMap)
        val savings = computeSavings(request, productMap)
        val created = OffsetDateTime.now()

        // Create the Order
        val order = OrderEntity(
            id = UUID.randomUUID().toString(),
            status = OrderStatus.CREATED,
            merchantId = request.merchantId,
            tenantId = securityManager.tenantId(),
            accountId = securityManager.accountId(),
            currency = products[0].currency,
            subTotalPrice = subTotal,
            deliveryFees = 0.0,
            savingsAmount = savings,
            created = created,
            expires = created.plusMinutes(30)
        )
        order.updateTotalPrice()
        orderDao.save(order)

        // Items
        order.items = request.items
            .map {
                val product = productMap[it.productId]!!
                itemDao.save(
                    OrderItemEntity(
                        productId = it.productId,
                        quantity = it.quantity,
                        unitPrice = product.price ?: 0.0,
                        currency = product.currency,
                        order = order,
                        unitComparablePrice = product.comparablePrice
                    )
                )
            }

        return order
    }

    private fun createReservation(order: OrderEntity): Long {
        try {
            return catalogApi.createReservation(
                CreateReservationRequest(
                    orderId = order.id!!,
                    products = order.items.map {
                        ReservationProduct(
                            productId = it.productId,
                            quantity = it.quantity
                        )
                    }
                )
            ).id
        } catch (ex: FeignException.Conflict) {
            val errorResponse = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            if (errorResponse.error.code == com.wutsi.ecommerce.catalog.error.ErrorURN.OUT_OF_STOCK_ERROR.urn)
                throw ConflictException(
                    error = Error(
                        code = ErrorURN.PRODUCT_AVAILABILITY_ERROR.urn,
                        downstreamCode = errorResponse.error.code,
                        data = errorResponse.error.data
                    )
                )
            else
                throw ConflictException(
                    error = Error(
                        code = ErrorURN.RESERVATION_ERROR.urn,
                        downstreamCode = errorResponse.error.code,
                        data = errorResponse.error.data
                    )
                )
        }
    }

    private fun findProducts(request: CreateOrderRequest): List<ProductSummary> {
        val requestedProductIds = request.items.map { it.productId }.distinct()
        val products = catalogApi.searchProducts(
            SearchProductRequest(
                productIds = requestedProductIds,
                limit = requestedProductIds.size
            )
        ).products

        // Missing products?
        val foundProductIds = products.map { it.id }
        val missingIds = requestedProductIds.filter { !foundProductIds.contains(it) }
        if (missingIds.isNotEmpty())
            throw BadRequestException(
                error = Error(
                    code = ErrorURN.INVALID_PRODUCT_ID.urn,
                    parameter = Parameter(
                        name = "items.productId",
                        value = missingIds,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD
                    )
                )
            )

        return products
    }

    private fun computeSubTotalPrice(request: CreateOrderRequest, productMap: Map<Long, ProductSummary>): Double =
        request.items.sumOf { it.quantity.toDouble() * (getComparablePrice(productMap[it.productId]!!) ?: 0.0) }

    private fun getComparablePrice(product: ProductSummary): Double? =
        product.comparablePrice ?: product.price

    private fun computeSavings(request: CreateOrderRequest, productMap: Map<Long, ProductSummary>): Double =
        request.items.sumOf { it.quantity.toDouble() * getSavings(productMap[it.productId]!!) }

    private fun getSavings(product: ProductSummary): Double =
        if (product.comparablePrice != null && product.price != null)
            max(0.0, product.comparablePrice!! - product.price!!)
        else
            0.0
}
