package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.SetShippingAddressDelegate
import com.wutsi.ecommerce.order.dto.SetAddressRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.String

@RestController
public class SetShippingAddressController(
    private val `delegate`: SetShippingAddressDelegate
) {
    @PostMapping("/v1/orders/{id}/shipping-address")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: String,
        @Valid @RequestBody
        request: SetAddressRequest
    ) {
        delegate.invoke(id, request)
    }
}
