package com.mywallet.infrastructure.owner.graphql

import com.mywallet.domain.entity.Owner

data class OwnerInput(val name: String) {
    fun asOwner(): Owner {
        return Owner(name = name, publicId = "")
    }
}

fun Owner.asOutput() = OwnerOutput(name = this.name, publicId = this.publicId)

data class OwnerOutput(val name: String, val publicId: String)