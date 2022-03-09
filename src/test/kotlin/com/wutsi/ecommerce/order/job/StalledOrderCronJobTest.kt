package com.wutsi.ecommerce.order.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ecommerce.order.delegate.CancelOrderDelegate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/StalledOrderCronJob.sql"])
internal class StalledOrderCronJobTest {

    @MockBean
    private lateinit var delegate: CancelOrderDelegate

    @Autowired
    private lateinit var job: StalledOrderCronJob

    @Test
    fun run() {
        job.run()

        verify(delegate, times(2)).invoke(any())
        verify(delegate).invoke("111")
        verify(delegate).invoke("900")
    }
}
