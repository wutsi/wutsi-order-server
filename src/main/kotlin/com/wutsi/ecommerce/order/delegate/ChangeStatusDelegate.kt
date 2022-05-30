package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.analytics.tracking.WutsiTrackingApi
import com.wutsi.analytics.tracking.dto.PushTrackRequest
import com.wutsi.analytics.tracking.dto.Track
import com.wutsi.analytics.tracking.entity.EventType
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dao.OrderStatusRepository
import com.wutsi.ecommerce.order.dto.ChangeStatusRequest
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.entity.OrderStatusEntity
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
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
public class ChangeStatusDelegate(
    private val statusDao: OrderStatusRepository,
    private val orderDao: OrderRepository,
    private val securityManager: SecurityManager,
    private val logger: KVLogger,
    private val eventStream: EventStream,
    private val catalogApi: WutsiCatalogApi,
    private val trackingApi: WutsiTrackingApi,
    private val tracingContext: TracingContext,
    private val httpRequest: HttpServletRequest
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ChangeStatusDelegate::class.java)
    }

    @Transactional
    public fun invoke(id: String, request: ChangeStatusRequest) {
        logger.add("new_status", request.status)

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
        securityManager.checkTenant(order)

        if (order.status.name.equals(request.status, true))
            return // Nothing
        else if (OrderStatus.CANCELLED.name.equals(request.status, true))
            cancel(order, request)
        else if (OrderStatus.OPENED.name.equals(request.status, true))
            open(order, request)
        else if (OrderStatus.DONE.name.equals(request.status, true))
            done(order, request)
        else if (OrderStatus.EXPIRED.name.equals(request.status, true))
            expire(order, request)
        else if (OrderStatus.READY_FOR_PICKUP.name.equals(request.status, true))
            readyForPickup(order, request)
        else if (OrderStatus.IN_TRANSIT.name.equals(request.status, true))
            inTransit(order, request)
        else if (OrderStatus.DELIVERED.name.equals(request.status, true))
            delivered(order, request)
        else
            invalidStatus(order.status, request)
    }

    private fun expire(order: OrderEntity, request: ChangeStatusRequest) {
        if (order.status != OrderStatus.CREATED)
            throw invalidStatus(order.status, request)

        changeStatus(order, request, null)
    }

    private fun cancel(order: OrderEntity, request: ChangeStatusRequest) {
        if (order.isClosed())
            throw invalidStatus(order.status, request)

        changeStatus(order, request, null)
        if (order.reservationId != null)
            catalogApi.cancelReservation(order.reservationId!!)
        publish(order, EventURN.ORDER_CANCELLED)
    }

    private fun open(order: OrderEntity, request: ChangeStatusRequest) {
        securityManager.ensureOwner(order)

        if (order.status != OrderStatus.CREATED)
            throw invalidStatus(order.status, request)

        changeStatus(order, request, null)
        trackNewOrder(order)
        publish(order, EventURN.ORDER_OPENED)
    }

    private fun done(order: OrderEntity, request: ChangeStatusRequest) {
        if (order.status != OrderStatus.OPENED)
            throw invalidStatus(order.status, request)

        changeStatus(order, request, EventURN.ORDER_DONE)
    }

    private fun readyForPickup(order: OrderEntity, request: ChangeStatusRequest) {
        if (order.status != OrderStatus.DONE)
            throw invalidStatus(order.status, request)

        changeStatus(order, request, EventURN.ORDER_READY_FOR_PICKUP)
    }

    private fun inTransit(order: OrderEntity, request: ChangeStatusRequest) {
        if (order.status != OrderStatus.DONE)
            throw invalidStatus(order.status, request)

        changeStatus(order, request, EventURN.ORDER_IN_TRANSIT)
    }

    private fun delivered(order: OrderEntity, request: ChangeStatusRequest) {
        if (order.status != OrderStatus.IN_TRANSIT && order.status != OrderStatus.READY_FOR_PICKUP)
            throw invalidStatus(order.status, request)

        changeStatus(order, request, EventURN.ORDER_DELIVERED)
    }

    private fun changeStatus(order: OrderEntity, request: ChangeStatusRequest, event: EventURN?) {
        val now = OffsetDateTime.now()

        val previousStatus = order.status
        order.status = OrderStatus.valueOf(request.status.uppercase())
        orderDao.save(order)

        statusDao.save(
            OrderStatusEntity(
                order = order,
                status = order.status,
                reason = request.reason,
                created = now,
                previousStatus = previousStatus,
                comment = request.comment
            )
        )

        if (event != null)
            publish(order, event)
    }

    private fun invalidStatus(status: OrderStatus, request: ChangeStatusRequest) = ConflictException(
        error = Error(
            code = ErrorURN.ILLEGAL_STATUS.urn,
            data = mapOf(
                "status" to status,
                "request_status" to request.status
            ),
            parameter = Parameter(
                name = "id",
                value = id,
                type = ParameterType.PARAMETER_TYPE_PATH,
            ),
        )
    )

    private fun publish(order: OrderEntity, event: EventURN) {
        try {
            eventStream.publish(event.urn, OrderEventPayload(order.id ?: ""))
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push event", ex)
        }
    }

    private fun trackNewOrder(order: OrderEntity) {
        order.items.forEach {
            try {
                trackingApi.push(
                    request = PushTrackRequest(
                        track = Track(
                            time = System.currentTimeMillis(),
                            tenantId = tracingContext.tenantId(),
                            deviceId = tracingContext.deviceId(),
                            productId = it.productId.toString(),
                            accountId = securityManager.accountId().toString(),
                            correlationId = tracingContext.traceId(),
                            merchantId = order.merchantId.toString(),
                            value = it.quantity * it.unitPrice,
                            event = EventType.ORDER.name,
                            ua = httpRequest.getHeader("User-Agent"),
                            ip = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr,
                            referer = httpRequest.getHeader("Referer")
                        )
                    )
                )
            } catch (ex: Exception) {
                LOGGER.warn("Unable to push tracking event", ex)
            }
        }
    }
}
