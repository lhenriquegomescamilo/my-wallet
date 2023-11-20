package com.mywallet.infrastructure.owner.graphql

import com.expediagroup.graphql.server.operations.Query
import com.mywallet.application.UseCase
import com.mywallet.domain.entity.Owner

class OwnerQuery(private val queryOwnerUseCase: UseCase<Owner>) : Query {

    suspend fun findOwnerBy(publicId: String): OwnerOutput? {
        return queryOwnerUseCase.execute(Owner(publicId = publicId, name = ""))
            .map { it.asOutput() }
            .getOrNull()
    }
}