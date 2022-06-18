package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.CreateOrderDelegate
import com.wutsi.ecommerce.order.dto.CreateOrderRequest
import com.wutsi.ecommerce.order.dto.CreateOrderResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateOrderController(
    public val `delegate`: CreateOrderDelegate,
) {
    @PostMapping("/v1/orders")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(@Valid @RequestBody request: CreateOrderRequest): CreateOrderResponse =
        delegate.invoke(request)
}
