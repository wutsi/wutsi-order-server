package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.event.EventURN
import org.junit.jupiter.api.Test

class ChangeStatusControllerDeliveredTest : AbstractChangeStatusControllerTest() {
    private val status = OrderStatus.DELIVERED
    private val event = EventURN.ORDER_DELIVERED

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
        changeStatusSuccessBadStatus("102", status)
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
        changeStatusSuccess("106", OrderStatus.IN_TRANSIT, status, event)
    }

    @Test
    fun delivered() {
        changeStatusSuccess("107", OrderStatus.DELIVERED, status, event)
    }
}
