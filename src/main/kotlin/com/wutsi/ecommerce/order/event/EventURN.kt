package com.wutsi.ecommerce.order.event

enum class EventURN(val urn: String) {
    ORDER_OPENED("urn:wutsi:event:order:order-opened"),
    ORDER_CANCELLED("urn:wutsi:event:order:order-cancelled"),
    ORDER_DONE("urn:wutsi:event:order:order-done"),
}
