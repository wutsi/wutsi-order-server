package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.AddressRepository
import com.wutsi.ecommerce.order.dto.ListAddressResponse
import com.wutsi.ecommerce.order.service.SecurityManager
import org.springframework.stereotype.Service

@Service
public class ListAddressesDelegate(
    private val dao: AddressRepository,
    private val securityManager: SecurityManager
) {
    public fun invoke(): ListAddressResponse {
        val addresses = dao.findByAccountIdAndTenantId(
            securityManager.accountId(),
            securityManager.tenantId()
        )
        return ListAddressResponse(
            addresses = addresses.map { it.toAddress() }
        )
    }
}
