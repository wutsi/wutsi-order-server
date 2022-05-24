package com.wutsi.ecommerce.order.job

import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.service.SecurityManager
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.tracing.DefaultTracingContext
import com.wutsi.platform.core.tracing.ThreadLocalTracingContextHolder
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

abstract class AbstractOrderCronJob : AbstractCronJob() {
    @Autowired
    protected lateinit var securityManager: SecurityManager

    protected fun initTracingContext(order: OrderEntity): TracingContext? {
        val tc = ThreadLocalTracingContextHolder.get()
        ThreadLocalTracingContextHolder.set(
            DefaultTracingContext(
                tenantId = order.tenantId.toString(),
                traceId = tc?.traceId() ?: UUID.randomUUID().toString(),
                deviceId = tc?.deviceId() ?: "NONE",
                clientId = tc?.clientId() ?: getJobName(),
                clientInfo = tc?.clientInfo() ?: getJobName()
            )
        )
        return tc
    }

    protected fun restoreTracingContext(tc: TracingContext?) {
        if (tc == null)
            ThreadLocalTracingContextHolder.remove()
        else
            ThreadLocalTracingContextHolder.set(tc)
    }
}
