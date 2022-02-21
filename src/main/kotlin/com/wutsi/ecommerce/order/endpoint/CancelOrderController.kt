package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.CancelOrderDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class CancelOrderController(
    private val `delegate`: CancelOrderDelegate
) {
    @DeleteMapping("/v1/orders/{id}")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(@PathVariable(name = "id") id: String) {
        delegate.invoke(id)
    }
}
