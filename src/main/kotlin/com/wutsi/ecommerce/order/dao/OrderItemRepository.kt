package com.wutsi.ecommerce.order.dao

import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderItemEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : CrudRepository<OrderItemEntity, Long> {
    fun findByOrder(order: OrderEntity): List<OrderItemEntity>
}
