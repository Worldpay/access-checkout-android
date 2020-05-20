package com.worldpay.access.checkout.model

/**
 * [CardValidationRule] stores a set of validation rule attributes
 *
 * @property matcher a regex which is used to match a field to this rule
 * @property validLengths a rule which defines the fields valid lengths
 */
data class CardValidationRule(
    val matcher: String? = null,
    val validLengths: List<Int>
)
