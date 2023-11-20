package com.mywallet.application.owner.gateways

import com.mywallet.domain.entity.Owner

interface QueryOwnerGateway {
    suspend fun findByPublicId(publicId: String): Owner

}
