package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.event.EventURN
import org.junit.jupiter.api.Test

class ChangeStatusControllerInTransitTest : AbstractChangeStatusControllerTest() {
    private val status = OrderStatus.IN_TRANSIT
    private val event = EventURN.ORDER_IN_TRANSIT

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
        changeStatusSuccessBadStatus("105", status)
    }

    @Test
    fun inTransit() {
        changeStatusSuccess("106", OrderStatus.IN_TRANSIT, status, event)
    }

    @Test
    fun delivered() {
        changeStatusSuccessBadStatus("107", status)
    }
}
