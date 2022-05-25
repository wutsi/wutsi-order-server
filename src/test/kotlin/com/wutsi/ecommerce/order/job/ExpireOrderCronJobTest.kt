package com.wutsi.ecommerce.order.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ecommerce.order.delegate.ChangeStatusDelegate
import com.wutsi.ecommerce.order.dto.ChangeStatusRequest
import com.wutsi.ecommerce.order.entity.OrderStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ExpireOrderCronJob.sql"])
internal class ExpireOrderCronJobTest {

    @MockBean
    private lateinit var delegate: ChangeStatusDelegate

    @Autowired
    private lateinit var job: ExpireOrderCronJob

    @Test
    fun run() {
        job.run()

        verify(delegate, times(2)).invoke(any(), any())
        verify(delegate).invoke("111", ChangeStatusRequest(OrderStatus.CANCELLED.name, "expired"))
        verify(delegate).invoke("900", ChangeStatusRequest(OrderStatus.CANCELLED.name, "expired"))
    }
}
