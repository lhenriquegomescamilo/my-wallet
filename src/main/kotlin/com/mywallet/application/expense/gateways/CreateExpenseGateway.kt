package com.mywallet.application.expense.gateways

import com.mywallet.application.Gateway
import com.mywallet.domain.entity.Expense

interface CreateExpenseGateway : Gateway {
    suspend fun create(expense: Expense): Expense
    suspend fun checkIfExists(input: Expense): Boolean
}
