package com.wutsi.ecommerce.order.dao

import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface OrderRepository : CrudRepository<OrderEntity, String> {
    fun findByStatusAndExpiresLessThan(status: OrderStatus, expires: OffsetDateTime, page: Pageable): List<OrderEntity>
}
