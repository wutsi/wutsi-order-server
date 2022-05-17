package com.wutsi.ecommerce.order.`delegate`

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
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SubmitOrderDelegate(
    private val orderDao: OrderRepository,
    private val logger: KVLogger,
    private val eventStream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubmitOrderDelegate::class.java)
    }

    fun invoke(id: String) {
        val order = orderDao.findById(id)
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

        logger.add("order_status", order.status)
        if (order.status == OrderStatus.CREATED) {
            order.status = OrderStatus.READY
            orderDao.save(order)
            publish(id)

            logger.add("order_submitted", true)
        } else if (order.status == OrderStatus.READY) {
            logger.add("order_submitted", false)
        } else {
            logger.add("order_submitted", false)
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
        }
    }

    private fun publish(id: String) {
        try {
            eventStream.publish(EventURN.ORDER_READY.urn, OrderEventPayload(id))
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push event", ex)
        }
    }
}
