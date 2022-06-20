package com.wutsi.ecommerce.order.entity

import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_ADDRESS")
data class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val tenantId: Long = -1,
    val accountId: Long = -1,

    val firstName: String = "",
    val lastName: String = "",
    val country: String? = null,
    val street: String? = null,
    val cityId: Long? = null,
    val zipCode: String? = null,
    val email: String? = null,

    @Enumerated
    val type: AddressType = AddressType.POSTAL,
)
