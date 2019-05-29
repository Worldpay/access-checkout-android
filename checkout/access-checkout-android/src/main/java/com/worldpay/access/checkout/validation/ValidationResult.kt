package com.worldpay.access.checkout.validation

/**
 * A [ValidationResult] can be partially and/or completely valid and/or invalid.
 *
 * Being [partial]ly valid refers to a field having not been completed yet and not yet in an invalid state for the
 * current field input
 *
 * Being [complete]ly valid refers to a field in a complete and valid state and ready to be
 * used in the generate session request
 */
data class ValidationResult(val partial: Boolean, val complete: Boolean)