package com.worldpay.access.checkout.model

data class CardBrand(
	val name: String,
	val image: String? = null,
	val cvv: CardValidationRule? = null,
	val pans: List<CardValidationRule>
)
