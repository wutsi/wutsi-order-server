package com.wutsi.ecommerce.order.event

enum class EventURN(val urn: String) {
    ORDER_READY("urn:wutsi:event:order:order-ready"),
    ORDER_CANCELLED("urn:wutsi:event:order:order-cancelled"),
}
