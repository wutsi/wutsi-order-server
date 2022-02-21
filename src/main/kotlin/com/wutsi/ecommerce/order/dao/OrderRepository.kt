package com.wutsi.ecommerce.order.dao

import com.wutsi.ecommerce.order.entity.OrderEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : CrudRepository<OrderEntity, String>
