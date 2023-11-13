package com.mywallet.infrastructure.category.graphql

import com.expediagroup.graphql.server.operations.Mutation
import com.mywallet.application.category.usecases.CreateCategoryUseCase
import com.mywallet.domain.entity.ValidationError
import graphql.GraphqlErrorException
import graphql.execution.DataFetcherResult

class CategoryMutation(private val categoryUseCase: CreateCategoryUseCase) : Mutation {

    suspend fun createCategory(input: CategoryInput): DataFetcherResult<CategoryOutput> {
        val domainModel = input.toDomainModel()
        return categoryUseCase.execute(domainModel)
            .map { it.toOutput() }
            .map { dataFetcherResult { data(it) } }
            .getOrElse { exception ->
                dataFetcherResult {
                    data(CategoryOutput(publicId = "", name = ""))
                    if (exception is ValidationError) {
                        val validationErrors =
                            exception.validations.map { errorMessage -> graphqlError { message(errorMessage.message) } }
                        errors(validationErrors)
                    } else {
                        error(
                            graphqlError { message(exception.localizedMessage) }
                        )
                    }

                }
            }
    }
}


fun <T> dataFetcherResult(fn: DataFetcherResult.Builder<T>.() -> Unit): DataFetcherResult<T> {
    return DataFetcherResult.newResult<T>()
        .apply(fn)
        .build()
}

fun graphqlError(fn: GraphqlErrorException.Builder.() -> Unit): GraphqlErrorException {
    return GraphqlErrorException.newErrorException().apply(fn).build()
}