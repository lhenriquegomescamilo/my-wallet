package com.mywallet.application.expense.usecases

import com.mywallet.domain.entity.Expense

data class Page<T>(private val data: List<T>, val counter: Int)

interface ExpenseQueryGateway {
     fun findExpensesPaged(offset: Int, limit: Int): Page<Expense>

 }
