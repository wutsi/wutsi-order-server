package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.order.event.EventURN
import com.wutsi.ecommerce.order.event.OrderEventPayload
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class CancelOrderDelegate(
    private val dao: OrderRepository,
    private val eventStream: EventStream,
    private val catalogApi: WutsiCatalogApi,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CancelOrderDelegate::class.java)
    }

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

        if (order.status == OrderStatus.CANCELLED) {
            // Already cancelled
        } else if (order.status == OrderStatus.COMPLETED) {
            // Order completed - cannot be cancelled
            throw ConflictException(
                error = Error(
                    code = ErrorURN.ILLEGAL_STATUS.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                    data = mapOf(
                        "status" to order.status
                    )
                )
            )
        } else {
            // Cancel the order
            order.status = OrderStatus.CANCELLED
            order.cancelled = OffsetDateTime.now()
            dao.save(order)

            // Cancel the reservation
            order.reservationId?.let {
                catalogApi.cancelReservation(it)
            }

            // Send event
            publish(id)
        }
    }

    private fun publish(id: String) {
        try {
            eventStream.publish(EventURN.ORDER_CANCELLED.urn, OrderEventPayload(id))
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push event", ex)
        }
    }
}
