package com.mywallet.infrastructure.owner.graphql

import com.expediagroup.graphql.server.operations.Mutation
import com.mywallet.application.owner.usecases.CreateOwnerUseCase
import com.mywallet.domain.entity.ValidationError
import com.mywallet.infrastructure.category.graphql.dataFetcherResult
import com.mywallet.infrastructure.category.graphql.graphqlError
import graphql.execution.DataFetcherResult

class OwnerMutation(private val useCase: CreateOwnerUseCase) : Mutation {
    suspend fun createOwner(input: OwnerInput): DataFetcherResult<OwnerOutput> {
        return input.asOwner()
            .let { useCase.execute(it) }
            .map { dataFetcherResult { data(it.asOutput()) } }
            .getOrElse { exception ->
                dataFetcherResult {
                    data(OwnerOutput(name = "", publicId = ""))

                    if (exception is ValidationError) {
                        exception.validations
                            .map { (message, _) -> graphqlError { message(message) } }
                            .let { error(it) }
                    } else {
                        error(
                            graphqlError { message(exception.localizedMessage) }
                        )
                    }
                }
            }


    }
}