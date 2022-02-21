package com.wutsi.ecommerce.order.error

enum class ErrorURN(val urn: String) {
    ORDER_NOT_FOUND("urn:wutsi:error:order:order-not-found"),
    INVALID_PRODUCT_ID("urn:wutsi:error:order:invalid-product-id"),
    PRODUCT_AVAILABILITY_ERROR("urn:wutsi:error:order:product-availability-error"),
    RESERVATION_ERROR("urn:wutsi:error:order:reservation-error"),
}
