package com.worldpay.access.checkout.model

data class CardDefaults(
	val pan: CardValidationRule?,
	val cvv: CardValidationRule?,
	val month: CardValidationRule?,
	val year: CardValidationRule?
)
