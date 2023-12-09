package com.mywallet.infrastructure.expense.graphql

import com.expediagroup.graphql.server.operations.Mutation
import com.mywallet.application.UseCase
import com.mywallet.domain.entity.Category
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ExpenseDescription
import com.mywallet.domain.entity.ExpenseStatus
import com.mywallet.domain.entity.ExpenseType
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.Price
import com.mywallet.domain.entity.ValidationError
import com.mywallet.infrastructure.category.graphql.dataFetcherResult
import com.mywallet.infrastructure.category.graphql.graphqlError
import graphql.execution.DataFetcherResult
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExpenseMutation(private val useCase: UseCase<Expense>) : Mutation {
    suspend fun createExpense(input: ExpenseInput): DataFetcherResult<ExpenseOutput> {
        return input.asDomain()
            .let { useCase.execute(it) }
            .map { dataFetcherResult { data(it.asOutput()) } }
            .getOrElse { exception ->
                dataFetcherResult {
                    data(ExpenseOutput(publicId = ""))

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

private fun Expense.asOutput(): ExpenseOutput {
    return ExpenseOutput(publicId = this.publicId)
}

data class ExpenseInput(
    val category: PublicId,
    val owner: PublicId,
    val price: PriceInput,
    val expireDate: ExpireDateInput,
    val type: String,
    val status: String,
    val description: ExpenseDescriptionInput,
) {
    fun asDomain(): Expense {
        return Expense(
            category = Category(publicId = category.publicId, name = ""),
            owner = Owner(publicId = owner.publicId, name = ""),
            price = Price(value = BigDecimal.valueOf(price.value), currencyMoney = price.currencyMoney),
            expireDate = LocalDate.parse(expireDate.date, DateTimeFormatter.ofPattern(expireDate.format)),
            paymentDate = null,
            type = ExpenseType.byNameIgnoreCaseOrEmpty(type),
            status = ExpenseStatus.EMPTY,
            description = ExpenseDescription(text = description.text)
        )
    }
}

data class PublicId(val publicId: String)
data class PriceInput(val value: Double, val currencyMoney: String)

data class ExpenseDescriptionInput(val text: String)

data class ExpireDateInput(val date: String, val format: String)

data class ExpenseOutput(val publicId: String)
