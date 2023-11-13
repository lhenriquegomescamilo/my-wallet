package com.mywallet.domain.entity

data class ErrorMessage(val message: String) {
}

data class ValidationError(val validations: List<ErrorMessage>): Exception()