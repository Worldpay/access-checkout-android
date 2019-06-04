package com.worldpay.access.checkout.model

/**
 * [CardValidationRule] stores a set of validation rule attributes
 *
 * @property matcher a regex which is used to match a field to this rule
 * @property minLength a rule which defines the fields minimum length
 * @property maxLength a rule which defines the fields maximum length
 * @property validLength a rule which defines the fields valid length
 * @property subRules a set of sub-rules for this parent rule. Empty by default
 */
data class CardValidationRule(
    val matcher: String?,
    val minLength: Int?,
    val maxLength: Int?,
    val validLength: Int?,
    val subRules: List<CardValidationRule> = emptyList()
)
