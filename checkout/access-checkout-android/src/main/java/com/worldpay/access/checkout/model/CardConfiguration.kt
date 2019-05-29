package com.worldpay.access.checkout.model

data class CardConfiguration(
	val brands: List<CardBrand>? = null,
	val defaults: CardDefaults? = null
) {

    fun isEmpty(): Boolean = brands == null && defaults == null
}
