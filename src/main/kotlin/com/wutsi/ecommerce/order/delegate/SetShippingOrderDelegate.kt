package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetShippingOrderRequest
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.order.service.SecurityManager
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
public class SetShippingOrderDelegate(
    private val orderDao: OrderRepository,
    private val securityManager: SecurityManager
) {
    public fun invoke(id: String, request: SetShippingOrderRequest) {
        val order = orderDao.findById(id)
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
        securityManager.checkTenant(order)
        order.ensureNotClosed()

        order.shippingOrderId = request.shippingOrderId
        orderDao.save(order)
    }
}
