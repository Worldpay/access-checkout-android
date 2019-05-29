package com.worldpay.access.checkout.model

data class CardValidationRule(
    val matcher: String?,
    val minLength: Int?,
    val maxLength: Int?,
    val validLength: Int?,
    val subRules: List<CardValidationRule> = emptyList()
)