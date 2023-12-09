package com.mywallet.infrastructure.expense.graphql

import com.expediagroup.graphql.server.operations.Query
import java.util.*

data class ExpenseOutputQuery(val publicId: String)
class ExpenseQuery : Query {

    fun findAllExpenses() = listOf(ExpenseOutputQuery(UUID.randomUUID().toString()))

}
