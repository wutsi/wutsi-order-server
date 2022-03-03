package com.wutsi.ecommerce.order.error

enum class ErrorURN(val urn: String) {
    ADDRESS_NOT_FOUND("urn:wutsi:error:order:address-not-found"),
    ORDER_NOT_FOUND("urn:wutsi:error:order:order-not-found"),
    SHIPPING_NOT_FOUND("urn:wutsi:error:order:shipping-not-found"),
    PRODUCT_NOT_FOUND("urn:wutsi:error:order:product-not-found"),
    ILLEGAL_STATUS("urn:wutsi:error:order:illegal-status"),
    ILLEGAL_ADDRESS_ACCESS("urn:wutsi:error:order:illegal-address-access"),
    INVALID_PRODUCT_ID("urn:wutsi:error:order:invalid-product-id"),
    PRODUCT_AVAILABILITY_ERROR("urn:wutsi:error:order:product-availability-error"),
    RESERVATION_ERROR("urn:wutsi:error:order:reservation-error"),
}
