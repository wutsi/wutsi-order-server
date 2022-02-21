package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.GetOrderResponse
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
public class GetOrderDelegate(private val orderDao: OrderRepository) {
    public fun invoke(id: Long): GetOrderResponse {
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

        return GetOrderResponse(
            order = order.toOrder()
        )
    }
}
