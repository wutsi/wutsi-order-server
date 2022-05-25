package com.wutsi.ecommerce.order.dao

import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatusEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderStatusRepository : CrudRepository<OrderStatusEntity, Long> {
    fun findByOrder(order: OrderEntity): List<OrderStatusEntity>
}
