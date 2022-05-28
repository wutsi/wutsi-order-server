package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.event.EventURN
import org.junit.jupiter.api.Test

class ChangeStatusControllerReadyForPickupTest : AbstractChangeStatusControllerTest() {
    private val status = OrderStatus.READY_FOR_PICKUP
    private val event = EventURN.ORDER_READY_FOR_PICKUP

    @Test
    fun created() {
        changeStatusSuccessBadStatus("100", status)
    }

    @Test
    fun opened() {
        changeStatusSuccessBadStatus("101", status)
    }

    @Test
    fun done() {
        changeStatusSuccess("102", OrderStatus.DONE, status, event)
    }

    @Test
    fun cancelled() {
        changeStatusSuccessBadStatus("103", status)
    }

    @Test
    fun expired() {
        changeStatusSuccessBadStatus("104", status)
    }

    @Test
    fun readyForPickup() {
        changeStatusSuccess("105", OrderStatus.READY_FOR_PICKUP, status, event)
    }

    @Test
    fun inTransit() {
        changeStatusSuccessBadStatus("106", status)
    }

    @Test
    fun delivered() {
        changeStatusSuccessBadStatus("107", status)
    }
}
