package com.wutsi.ecommerce.order.service

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
}
