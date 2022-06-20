package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.AddressRepository
import com.wutsi.ecommerce.order.dto.CreateAddressRequest
import com.wutsi.ecommerce.order.dto.CreateAddressResponse
import com.wutsi.ecommerce.order.entity.AddressEntity
import com.wutsi.ecommerce.order.entity.AddressType
import com.wutsi.ecommerce.order.service.SecurityManager
import org.springframework.stereotype.Service

@Service
public class CreateAddressDelegate(
    private val dao: AddressRepository,
    private val securityManager: SecurityManager
) {
    public fun invoke(request: CreateAddressRequest): CreateAddressResponse {
        val address = dao.save(
            AddressEntity(
                firstName = request.firstName,
                lastName = request.lastName,
                zipCode = request.zipCode,
                street = request.street,
                country = request.country,
                cityId = request.cityId,
                accountId = securityManager.accountId()!!,
                tenantId = securityManager.tenantId()!!,
                email = request.email,
                type = AddressType.valueOf(request.type.uppercase())
            )
        )
        return CreateAddressResponse(id = address.id!!)
    }
}
