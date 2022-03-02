package com.wutsi.ecommerce.order.dao

import com.wutsi.ecommerce.order.entity.AddressEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : CrudRepository<AddressEntity, Long> {
    fun findByAccountIdAndTenantId(accountId: Long, tenantId: Long): List<AddressEntity>
}
