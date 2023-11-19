package com.mywallet.application.owner.gateways

import com.mywallet.domain.entity.Owner

interface CreateOwnerGateway {
    suspend fun save(input: Owner): Owner

}
