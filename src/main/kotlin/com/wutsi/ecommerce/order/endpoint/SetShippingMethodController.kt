package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.SetShippingMethodDelegate
import com.wutsi.ecommerce.order.dto.SetShippingMethodRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.String

@RestController
public class SetShippingMethodController(
    public val `delegate`: SetShippingMethodDelegate,
) {
    @PostMapping("/v1/orders/{id}/shipping-method")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: String,
        @Valid @RequestBody
        request: SetShippingMethodRequest
    ) {
        delegate.invoke(id, request)
    }
}
