package com.mywallet.application.expense.usecases

import com.mywallet.domain.entity.Expense

class QueryExpenseUseCase(private val query: ExpenseQueryGateway) {
    fun execute(offset: Int, limit: Int): Page<Expense> {
        val page = query.findExpensesPaged(offset, limit)
        return page
    }

}
