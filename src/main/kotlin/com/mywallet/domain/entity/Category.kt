package com.mywallet.domain.entity

import java.util.UUID

data class Category(val publicId: String = UUID.randomUUID().toString(), val name: String)
