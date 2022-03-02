package com.wutsi.ecommerce.order.`delegate`

import com.wutsi.ecommerce.order.dao.AddressRepository
import com.wutsi.ecommerce.order.dao.OrderRepository
import com.wutsi.ecommerce.order.dto.SetAddressRequest
import com.wutsi.ecommerce.order.entity.AddressEntity
import com.wutsi.ecommerce.order.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
public class SetShippingAddressDelegate(
    private val dao: OrderRepository,
    private val addressDao: AddressRepository
) {
    public fun invoke(id: String, request: SetAddressRequest) {
        val order = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ORDER_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PAYLOAD
                        )
                    )
                )
            }

        order.ensureNotClosed()

        // Create the address
        val address = addressDao.save(
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

        // Update the order
        order.shippingAddress = address
        dao.save(order)
    }
}
