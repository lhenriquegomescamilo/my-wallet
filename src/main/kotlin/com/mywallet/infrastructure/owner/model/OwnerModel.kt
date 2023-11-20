package com.mywallet.infrastructure.owner.model

import com.mywallet.domain.entity.Owner

data class OwnerModel(val name: String, val publicId: String) {
    fun asDomain() = Owner(publicId = this.publicId, name = this.name)
}