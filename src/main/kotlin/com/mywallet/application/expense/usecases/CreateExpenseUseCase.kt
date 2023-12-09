package com.mywallet.application.expense.usecases

import com.mywallet.application.UseCase
import com.mywallet.application.ValidationGateway
import com.mywallet.application.expense.exceptions.ExpenseAlreadyExists
import com.mywallet.application.expense.gateways.CreateExpenseGateway
import com.mywallet.domain.entity.Expense
import com.mywallet.domain.entity.ValidationError

class CreateExpenseUseCase(
    private val expenseGatewayRepository: CreateExpenseGateway,
    private val expenseValidation: ValidationGateway<Expense>
) : UseCase<Expense>() {
    override suspend fun execute(input: Expense): Result<Expense> {
        val (validations) = expenseValidation.validate(input)
        if (validations.isNotEmpty()) {
            return Result.failure(ValidationError(validations))
        }
        if (expenseGatewayRepository.checkIfExists(input)) {
            return Result.failure(ExpenseAlreadyExists())
        }
        return Result.success(expenseGatewayRepository.create(input))
    }

}