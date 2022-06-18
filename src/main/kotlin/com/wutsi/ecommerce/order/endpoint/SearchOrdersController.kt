package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.SearchOrdersDelegate
import com.wutsi.ecommerce.order.dto.SearchOrderRequest
import com.wutsi.ecommerce.order.dto.SearchOrderResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchOrdersController(
    public val `delegate`: SearchOrdersDelegate,
) {
    @PostMapping("/v1/orders/search")
    @PreAuthorize(value = "hasAuthority('order-read')")
    public fun invoke(@Valid @RequestBody request: SearchOrderRequest): SearchOrderResponse =
        delegate.invoke(request)
}
