package com.wutsi.ecommerce.order.config

import com.wutsi.ecommerce.order.service.ApplicationTokenInitializer
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class ApplicationTokenConfiguration(
    private val initializer: ApplicationTokenInitializer
) {
    @PostConstruct
    fun init() {
        initializer.init()
    }
}
