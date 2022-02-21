package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.GetOrderDelegate
import com.wutsi.ecommerce.order.dto.GetOrderResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetOrderController(
    private val `delegate`: GetOrderDelegate
) {
    @GetMapping("/v1/orders/{id}")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(@PathVariable(name = "id") id: Long): GetOrderResponse = delegate.invoke(id)
}
