package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingMethodRequest
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class SetShippingMethodDelegate(
    private val dao: OrderRepository,
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

        order.shippingId = request.shippingId
        order.deliveryFees = request.deliveryFees
        order.expectedDelivered = request.deliveryTime?.let { OffsetDateTime.now().plusHours(it.toLong()) }
        order.updateTotalPrice()
        dao.save(order)

        logger.add("expected_delivered", order.expectedDelivered)
        logger.add("delivery_fees", order.deliveryFees)
    }
}
