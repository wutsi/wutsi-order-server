package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dto.SearchOrderRequest
import com.wutsi.ecommerce.order.dto.SearchOrderResponse
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.service.SecurityManager
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class SearchOrdersDelegate(
    private val securityManager: SecurityManager,
    private val em: EntityManager,
) {
    fun invoke(request: SearchOrderRequest): SearchOrderResponse {
        val query = em.createQuery(sql(request))
        parameters(request, query)
        val orders = query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<OrderEntity>

        return SearchOrderResponse(
            orders = orders.map { it.toOrderSummary() }
        )
    }

    private fun sql(request: SearchOrderRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty())
            select
        else
            "$select WHERE $where ORDER BY O.created DESC"
    }

    private fun select(): String =
        "SELECT O FROM OrderEntity O"

    private fun where(request: SearchOrderRequest): String {
        val criteria = mutableListOf<String>()

        criteria.add("O.tenantId = :tenant_id")
        if (request.accountId != null)
            criteria.add("O.accountId = :account_id")

        if (request.merchantId != null)
            criteria.add("O.merchantId = :merchant_id")

        if (request.status.isNotEmpty())
            criteria.add("O.status IN :status")

        if (request.createdFrom != null)
            criteria.add("O.created >= :created_from")

        if (request.createdTo != null)
            criteria.add("O.created <= :created_to")

        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchOrderRequest, query: Query) {
        query.setParameter("tenant_id", securityManager.tenantId())

        if (request.accountId != null)
            query.setParameter("account_id", request.accountId)

        if (request.merchantId != null)
            query.setParameter("merchant_id", request.merchantId)

        if (request.status.isNotEmpty())
            query.setParameter("status", request.status.map { OrderStatus.valueOf(it) })

        if (request.createdFrom != null)
            query.setParameter("created_from", request.createdFrom)

        if (request.createdTo != null)
            query.setParameter("created_to", request.createdTo)
    }
}
