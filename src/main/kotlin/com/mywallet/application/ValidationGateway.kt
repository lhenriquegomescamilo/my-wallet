package com.mywallet.application

import com.mywallet.domain.entity.ErrorMessage

interface ValidationGateway<T> {
    suspend fun validate(input: T): Pair<List<ErrorMessage>, T>
}
