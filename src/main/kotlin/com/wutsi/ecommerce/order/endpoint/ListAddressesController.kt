package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.delegate.ListAddressesDelegate
import com.wutsi.ecommerce.order.dto.ListAddressResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
public class ListAddressesController(
    public val `delegate`: ListAddressesDelegate,
) {
    @GetMapping("/v1/addresses")
    @PreAuthorize(value = "hasAuthority('order-read')")
    public fun invoke(@RequestParam(name = "type") type: String? = null): ListAddressResponse =
        delegate.invoke(type)
}
