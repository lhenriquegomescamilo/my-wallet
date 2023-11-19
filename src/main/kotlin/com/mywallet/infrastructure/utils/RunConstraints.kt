package com.mywallet.infrastructure.utils

import com.mywallet.domain.entity.Constraints
import com.mywallet.domain.entity.ErrorMessage

fun <T> runConstraints(input: T, fn: () -> List<Constraints>): Pair<List<ErrorMessage>, T> {
    return Pair(fn().map { ErrorMessage(it.name, it.key) }, input)
}