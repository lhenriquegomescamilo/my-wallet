package com.mywallet.application.expense.usecases

import com.mywallet.domain.entity.Expense
import com.mywallet.infrastructure.expense.graphql.ExpenseOutput

open class Page<T>(open val data: List<T>, open val counter: Int) {
}

data class PageExpense(override val data: List<Expense>, override val counter: Int) : Page<Expense>(data, counter)

data class PageExpenseOutput(override val data: List<ExpenseOutput>, override val counter: Int) : Page<ExpenseOutput>(data, counter)

interface ExpenseQueryGateway {
    fun findExpensesPaged(offset: Int, limit: Int): PageExpense

}
