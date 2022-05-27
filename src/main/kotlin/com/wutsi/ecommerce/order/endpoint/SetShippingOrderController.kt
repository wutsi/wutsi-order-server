package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.SetShippingOrderDelegate
import com.wutsi.ecommerce.order.dto.SetShippingOrderRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.String

@RestController
public class SetShippingOrderController(
    private val `delegate`: SetShippingOrderDelegate
) {
    @PostMapping("/v1/orders/{id}/shipping-order")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: String,
        @Valid @RequestBody
        request: SetShippingOrderRequest
    ) {
        delegate.invoke(id, request)
    }
}
