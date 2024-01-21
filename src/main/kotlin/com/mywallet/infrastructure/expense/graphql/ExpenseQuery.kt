package com.mywallet.infrastructure.expense.graphql

import com.expediagroup.graphql.server.operations.Query
import com.mywallet.application.expense.usecases.PageExpenseOutput
import com.mywallet.application.expense.usecases.QueryExpenseUseCase


data class ExpenseOutputQuery(val publicId: String)
class ExpenseQuery(private val queryExpenseUseCase: QueryExpenseUseCase) : Query {

    fun findExpenses(offset: Int, limit: Int): PageExpenseOutput {
        val page = queryExpenseUseCase.execute(offset, limit)
        val expenseOutputs = page.data.map { it.asOutput() }
        return PageExpenseOutput(expenseOutputs, page.counter)
    }

}
