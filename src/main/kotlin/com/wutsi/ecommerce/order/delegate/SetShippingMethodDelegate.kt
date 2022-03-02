package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingMethodRequest
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SetShippingMethodDelegate(
    private val dao: OrderRepository
) {
    @Transactional
    fun invoke(id: String, request: SetShippingMethodRequest) {
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

        order.deliveryFees = request.shippingRate
        order.shippingId = request.shippingId
        dao.save(order)
    }
}
