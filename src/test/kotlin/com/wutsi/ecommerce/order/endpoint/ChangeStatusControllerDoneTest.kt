package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.event.EventURN
import org.junit.jupiter.api.Test

class ChangeStatusControllerDoneTest : AbstractChangeStatusControllerTest() {
    private val status = OrderStatus.DONE
    private val event = EventURN.ORDER_DONE

    @Test
    fun created() {
        changeStatusSuccessBadStatus("100", status)
    }

    @Test
    fun opened() {
        changeStatusSuccess("101", OrderStatus.OPENED, status, event)
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
        changeStatusSuccessBadStatus("106", status)
    }

    @Test
    fun delivered() {
        changeStatusSuccessBadStatus("107", status)
    }
}
