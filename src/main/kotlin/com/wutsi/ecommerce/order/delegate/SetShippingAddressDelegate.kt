package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.AddressRepository
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetAddressRequest
import com.wutsi.ecommerce.order.entity.AddressEntity
import com.wutsi.ecommerce.order.entity.OrderEntity
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.ecommerce.order.service.SecurityManager
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class SetShippingAddressDelegate(
    private val dao: OrderRepository,
    private val addressDao: AddressRepository,
    private val securityManager: SecurityManager
) {
    fun invoke(id: String, request: SetAddressRequest) {
        val order = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ORDER_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH
                        )
                    )
                )
            }
        securityManager.checkTenant(order)
        order.ensureNotClosed()

        order.shippingAddress = getAddress(order, request)
        dao.save(order)
    }

    private fun getAddress(order: OrderEntity, request: SetAddressRequest): AddressEntity {
        val address = if (request.id != null)
            addressDao.findById(request.id)
                .orElseThrow {
                    NotFoundException(
                        error = Error(
                            code = ErrorURN.ADDRESS_NOT_FOUND.urn,
                            parameter = Parameter(
                                name = "id",
                                value = request.id,
                                type = ParameterType.PARAMETER_TYPE_PAYLOAD
                            )
                        )
                    )
                }
        else
            addressDao.save(
                AddressEntity(
                    firstName = request.firstName,
                    lastName = request.lastName,
                    zipCode = request.zipCode,
                    street = request.street,
                    country = request.country,
                    cityId = request.cityId,
                    accountId = order.accountId,
                    tenantId = order.tenantId,
                    email = request.email,
                )
            )

        if (address.accountId != order.accountId || address.tenantId != order.tenantId)
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.ILLEGAL_ADDRESS_ACCESS.urn,
                )
            )

        return address
    }
}
