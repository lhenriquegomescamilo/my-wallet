package com.mywallet.application.owner.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.owner.gateways.QueryOwnerGateway
import com.mywallet.domain.entity.Owner

class QueryOwnerUseCase(private val queryOwnerGateway: QueryOwnerGateway): UseCase<Owner>() {
    override suspend fun execute(input: Owner): Result<Owner> {
        return runCatching { queryOwnerGateway.findByPublicId(input.publicId) }
    }
}