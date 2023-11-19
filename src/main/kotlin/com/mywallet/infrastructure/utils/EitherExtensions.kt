package com.mywallet.infrastructure.utils

import arrow.core.Either

fun <C, T> Either<C, T>.leftOrEmpty() = this.leftOrNull()?.let(::listOf) ?: emptyList()