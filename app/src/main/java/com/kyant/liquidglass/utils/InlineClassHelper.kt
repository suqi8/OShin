package com.kyant.liquidglass.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal fun throwIllegalArgumentException(message: String) {
    throw IllegalArgumentException(message)
}

@OptIn(ExperimentalContracts::class)
internal inline fun requirePrecondition(value: Boolean, lazyMessage: () -> String) {
    contract { returns() implies value }
    if (!value) {
        throwIllegalArgumentException(lazyMessage())
    }
}
