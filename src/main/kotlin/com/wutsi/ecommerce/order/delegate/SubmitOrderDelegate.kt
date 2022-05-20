package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.analytics.tracking.WutsiTrackingApi
import com.wutsi.analytics.tracking.dto.PushTrackRequest
import com.wutsi.analytics.tracking.dto.Track
import com.wutsi.analytics.tracking.entity.EventType
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.order.event.EventURN
import com.wutsi.ecommerce.order.event.OrderEventPayload
import com.wutsi.ecommerce.order.service.SecurityManager
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SubmitOrderDelegate(
    private val trackingApi: WutsiTrackingApi,
    private val orderDao: OrderRepository,
    private val logger: KVLogger,
    private val eventStream: EventStream,
    private val request: HttpServletRequest,
    private val tracingContext: TracingContext,
    private val securityManager: SecurityManager,
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
            publish(order)
            track(order)

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

    private fun publish(order: OrderEntity) {
        try {
            eventStream.publish(EventURN.ORDER_READY.urn, OrderEventPayload(order.id ?: "-"))
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push the event", ex)
        }
    }

    private fun track(order: OrderEntity) {
        order.items.forEach {
            trackingApi.push(
                request = PushTrackRequest(
                    track = Track(
                        time = System.currentTimeMillis(),
                        tenantId = securityManager.tenantId().toString(),
                        deviceId = tracingContext.deviceId(),
                        productId = it.productId.toString(),
                        accountId = securityManager.accountId().toString(),
                        correlationId = tracingContext.traceId(),
                        merchantId = order.merchantId.toString(),
                        value = it.quantity * it.unitPrice,
                        event = EventType.ORDER.name,
                        ua = request.getHeader("User-Agent"),
                        ip = request.getHeader("X-Forwarded-For") ?: request.remoteAddr,
                        referer = request.getHeader("Referer")
                    )
                )
            )
        }
    }
}
