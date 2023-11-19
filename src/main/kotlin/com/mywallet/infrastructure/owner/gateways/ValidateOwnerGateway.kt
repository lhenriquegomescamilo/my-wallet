package com.mywallet.infrastructure.owner.gateways

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.mywallet.application.ValidationGateway
import com.mywallet.domain.entity.Owner
import com.mywallet.domain.entity.OwnerConstraint
import com.mywallet.infrastructure.utils.leftOrEmpty
import com.mywallet.infrastructure.utils.runConstraints

class ValidateOwnerGateway : ValidationGateway<Owner> {
    override suspend fun validate(input: Owner) = runConstraints(input) {
        validateName(input).leftOrEmpty()
    }

    private fun validateName(input: Owner) = either {
        ensure(input.name.isNotEmpty()) { OwnerConstraint.OwnerEmptyNameConstraints() }
        input
    }
}

