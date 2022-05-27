package com.wutsi.ecommerce.order.service

import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.security.WutsiPrincipal
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityManager(
    private val tracingContext: TracingContext
) {
    fun tenantId(): Long =
        tracingContext.tenantId()!!.toLong()

    fun accountId(): Long =
        principal().id.toLong()

    private fun principal(): WutsiPrincipal {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        return principal as WutsiPrincipal
    }

    fun ensureOwner(order: OrderEntity) {
        if (order.accountId != accountId())
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.PERMISSION_DENIED.urn
                )
            )
    }

    fun checkTenant(order: OrderEntity) {
        if (order.tenantId != tenantId())
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.ILLEGAL_TENANT_ACCESS.urn
                )
            )
    }

    fun ensureMerchant(order: OrderEntity) {
        if (order.accountId != accountId())
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.PERMISSION_DENIED.urn
                )
            )
    }
}
