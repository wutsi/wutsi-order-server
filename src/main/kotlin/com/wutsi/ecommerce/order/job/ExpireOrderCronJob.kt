package com.wutsi.ecommerce.order.job

import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.delegate.CancelOrderDelegate
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ExpireOrderCronJob(
    private val dao: OrderRepository,
    private val delegate: CancelOrderDelegate
) : AbstractOrderCronJob() {
    override fun getJobName(): String = "expire-order"

    @Scheduled(cron = "\${wutsi.application.jobs.expire-order.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        var count = 0L
        val size = 100
        var page = 0
        val now = OffsetDateTime.now()
        while (true) {
            val pagination = PageRequest.of(page, size)
            val orders = dao.findByStatusAndExpiresLessThan(OrderStatus.CREATED, now, pagination)
            orders.forEach {
                val tc = initTracingContext(it)
                try {
                    cancel(it)
                    count++
                } finally {
                    restoreTracingContext(tc)
                }
            }

            if (orders.isEmpty())
                break
            else
                page += size
        }
        return count
    }

    private fun cancel(order: OrderEntity) {
        val logger = DefaultKVLogger()
        logger.add("order_id", order.id)
        logger.add("order_created", order.created)
        logger.add("order_expires", order.expires)
        logger.add("job", getJobName())
        try {
            delegate.invoke(order.id ?: "")
        } catch (ex: Exception) {
            logger.setException(ex)
        } finally {
            logger.log()
        }
    }
}
