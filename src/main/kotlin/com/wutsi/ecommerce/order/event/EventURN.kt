package com.wutsi.ecommerce.order.event

enum class EventURN(val urn: String) {
    ORDER_OPENED("urn:wutsi:event:order:order-opened"),
    ORDER_CANCELLED("urn:wutsi:event:order:order-cancelled"),
    ORDER_DONE("urn:wutsi:event:order:order-done"),
    ORDER_READY_FOR_PICKUP("urn:wutsi:event:order:order-ready-for-pickup"),
    ORDER_IN_TRANSIT("urn:wutsi:event:order:order-in-transit"),
    ORDER_DELIVERED("urn:wutsi:event:order:order-delivered"),
}
