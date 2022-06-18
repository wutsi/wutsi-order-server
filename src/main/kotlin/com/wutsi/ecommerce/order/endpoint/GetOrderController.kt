package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.GetOrderDelegate
import com.wutsi.ecommerce.order.dto.GetOrderResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class GetOrderController(
    public val `delegate`: GetOrderDelegate,
) {
    @GetMapping("/v1/orders/{id}")
    @PreAuthorize(value = "hasAuthority('order-read')")
    public fun invoke(@PathVariable(name = "id") id: String): GetOrderResponse = delegate.invoke(id)
}
