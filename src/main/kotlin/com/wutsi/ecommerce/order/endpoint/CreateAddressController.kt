package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.CreateAddressDelegate
import com.wutsi.ecommerce.order.dto.CreateAddressRequest
import com.wutsi.ecommerce.order.dto.CreateAddressResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateAddressController(
    public val `delegate`: CreateAddressDelegate,
) {
    @PostMapping("/v1/addresses")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(@Valid @RequestBody request: CreateAddressRequest): CreateAddressResponse =
        delegate.invoke(request)
}
