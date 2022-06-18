package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.`delegate`.ChangeStatusDelegate
import com.wutsi.ecommerce.order.dto.ChangeStatusRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.String

@RestController
public class ChangeStatusController(
    public val `delegate`: ChangeStatusDelegate,
) {
    @PostMapping("/v1/orders/{id}/status")
    @PreAuthorize(value = "hasAuthority('order-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: String,
        @Valid @RequestBody
        request: ChangeStatusRequest
    ) {
        delegate.invoke(id, request)
    }
}
