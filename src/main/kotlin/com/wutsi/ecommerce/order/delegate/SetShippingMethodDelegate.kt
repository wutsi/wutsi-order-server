package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.SearchProductRequest
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingMethodRequest
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.shipping.WutsiShippingApi
import com.wutsi.ecommerce.shipping.dto.Product
import com.wutsi.ecommerce.shipping.dto.RateSummary
import com.wutsi.ecommerce.shipping.dto.SearchRateRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class SetShippingMethodDelegate(
    private val dao: OrderRepository,
    private val shippingApi: WutsiShippingApi,
    private val catalogApi: WutsiCatalogApi,
    private val logger: KVLogger
) {
    @Transactional
    fun invoke(id: String, request: SetShippingMethodRequest) {
        logger.add("shipping_id", request.shippingId)

        val order = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ORDER_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PAYLOAD
                        )
                    )
                )
            }

        order.ensureNotClosed()
        logger.add("shipping_country", order.shippingAddress?.country)
        logger.add("shiping_city_id", order.shippingAddress?.cityId)

        val rate = findRate(order, request)
        order.expectedDelivered = rate.deliveryTime?.let { OffsetDateTime.now().plusHours(it.toLong()) }
        order.shippingId = request.shippingId
        order.deliveryFees = rate.rate
        order.updateTotalPrice()
        dao.save(order)

        logger.add("expected_delivered", order.expectedDelivered)
        logger.add("delivery_fees", order.deliveryFees)
    }

    private fun findRate(order: OrderEntity, request: SetShippingMethodRequest): RateSummary {
        // Get the product info
        val products = catalogApi.searchProducts(
            SearchProductRequest(
                productIds = order.items.map { it.productId }
            )
        ).products.associateBy { it.id }

        // Query the shipping rates
        val rates = shippingApi.searchRate(
            SearchRateRequest(
                shippingId = request.shippingId,
                accountId = order.merchantId,
                country = order.shippingAddress?.country ?: "",
                cityId = order.shippingAddress?.cityId,
                products = order.items.map {
                    val product = products[it.productId]
                        ?: throw ConflictException(
                            error = Error(
                                code = ErrorURN.PRODUCT_NOT_FOUND.urn,
                                data = mapOf("productId" to it.productId)
                            )
                        )

                    Product(
                        productId = it.productId,
                        productType = product.type
                    )
                }
            )
        ).rates

        // Result
        if (rates.isEmpty())
            ConflictException(
                error = Error(
                    code = ErrorURN.SHIPPING_NOT_FOUND.urn,
                    parameter = Parameter(
                        name = "shippingId",
                        value = request.shippingId,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD
                    )
                )
            )
        return rates[0]
    }
}
