package com.mywallet.infrastructure.expense.graphql

import com.expediagroup.graphql.server.operations.Query
import com.mywallet.application.expense.usecases.Page
import com.mywallet.application.expense.usecases.QueryExpenseUseCase
import com.mywallet.domain.entity.Expense


data class ExpenseOutputQuery(val publicId: String)
class ExpenseQuery(val queryExpenseUseCase: QueryExpenseUseCase) : Query {

    fun findExpenses(offset: Int, limit: Int): Page<Expense> {
        return queryExpenseUseCase.execute(offset, limit)
    }

}
