package com.mywallet.application.expense.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.expense.exceptions.ExpenseAlreadyExists
import com.mywallet.application.expense.gateways.CreateExpenseGateway
import com.mywallet.domain.entity.Expense

class CreateExpenseUseCase(private val expenseGateway: CreateExpenseGateway): UseCase<Expense>() {
    override suspend fun execute(input: Expense): Result<Expense> {
        if(expenseGateway.checkIfExists(input)) {
            return Result.failure(ExpenseAlreadyExists())
        }
        val expenseCreated = expenseGateway.create(input)
        return Result.success(expenseCreated)
    }
}