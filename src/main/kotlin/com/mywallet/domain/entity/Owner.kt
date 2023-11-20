package com.mywallet.domain.entity

data class Owner(val publicId: String = "", val name: String)

sealed class OwnerConstraint(name: String = "", key: String = "") : Constraints(name, key) {
    class OwnerEmptyNameConstraints(
        override val name: String = "name could not be empty",
        override val key: String = "name"
    ) : OwnerConstraint(name, key)
}