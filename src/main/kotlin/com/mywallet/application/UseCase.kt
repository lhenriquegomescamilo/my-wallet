package com.mywallet.application

abstract class UseCase<T> {

    abstract suspend fun execute(input: T): Result<T>
}
