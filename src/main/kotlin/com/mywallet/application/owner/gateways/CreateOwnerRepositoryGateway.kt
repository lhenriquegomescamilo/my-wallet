package com.mywallet.application.owner.gateways

import com.mywallet.domain.entity.Owner

interface CreateOwnerRepositoryGateway {
    suspend fun create(input: Owner): Owner

}
