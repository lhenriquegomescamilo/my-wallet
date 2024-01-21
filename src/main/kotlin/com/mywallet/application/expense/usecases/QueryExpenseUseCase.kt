package com.mywallet.application.expense.usecases

class QueryExpenseUseCase(private val query: ExpenseQueryGateway) {
    fun execute(offset: Int, limit: Int): PageExpense {
        val page = query.findExpensesPaged(offset, limit)
        return page
    }

}
