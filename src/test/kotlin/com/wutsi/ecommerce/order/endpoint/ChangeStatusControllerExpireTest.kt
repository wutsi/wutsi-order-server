package com.wutsi.ecommerce.order.endpoint

import com.wutsi.ecommerce.order.entity.OrderStatus
import com.wutsi.ecommerce.order.event.EventURN
import org.junit.jupiter.api.Test

class ChangeStatusControllerExpireTest : AbstractChangeStatusControllerTest() {
    private val status = OrderStatus.EXPIRED
    private val event: EventURN? = null

    @Test
    fun created() {
        changeStatusSuccess("100", OrderStatus.CREATED, status, event)
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
        changeStatusSuccess("104", OrderStatus.EXPIRED, status, event)
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
