package com.mywallet.domain.entity

import java.util.*

data class Owner(val publicId: String = UUID.randomUUID().toString(), val name: String)

sealed class OwnerConstraint(name: String = "", key: String = "") : Constraints(name, key) {
    class OwnerEmptyNameConstraints(
        override val name: String = "name could not be empty",
        override val key: String = "name"
    ) : OwnerConstraint(name, key)
}