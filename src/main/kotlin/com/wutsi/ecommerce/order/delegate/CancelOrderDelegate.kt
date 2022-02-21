package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class CancelOrderDelegate(
    private val catalogApi: WutsiCatalogApi,
    private val dao: OrderRepository
) {
    @Transactional
    fun invoke(id: String) {
        val order = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ORDER_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH
                        )
                    )
                )
            }

        if (order.status == OrderStatus.CANCELLED)
            return

        // Cancel the order
        order.status = OrderStatus.CANCELLED
        order.cancelled = OffsetDateTime.now()
        dao.save(order)

        // Cancel the reservation
        if (order.reservationId != null)
            catalogApi.cancelReservation(order.reservationId!!)
    }
}
