package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.SubmitOrderDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class SubmitOrderController(
    private val `delegate`: SubmitOrderDelegate
) {
    @GetMapping("/v1/orders/{id}/submit")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(@PathVariable(name = "id") id: String) {
        delegate.invoke(id)
    }
}
