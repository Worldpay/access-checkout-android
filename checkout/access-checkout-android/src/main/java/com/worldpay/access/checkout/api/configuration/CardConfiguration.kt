package com.worldpay.access.checkout.api.configuration

/**
 * Stores the configuration for a card for use by the card validation logic
 *
 * @property brands a list of card brand configuration
 * @property defaults a list of default configuration to use
 */
data class CardConfiguration(val brands: List<RemoteCardBrand>, val defaults: CardDefaults)

/**
 * Representation of a card brand
 *
 * @property name of the card brand
 * @property images a list of [RemoteCardBrandImage]'s
 * @property cvv validation rule for the cvv field
 * @property pan list of validations rules for the pan field
 */
data class RemoteCardBrand(
    val name: String,
    val images: List<RemoteCardBrandImage>,
    val cvv: CardValidationRule,
    val pan: CardValidationRule
)

/**
 * [RemoteCardBrandImage] stores the reference to an image resource
 * for a particular [RemoteCardBrand]. You can use the url reference
 * to the [RemoteCardBrand] to fetch it and apply it to the UI when the
 * brand has been identified
 *
 * @param type the media type of the image, e.g. 'image/svg+xml'
 * @param url the URL to where the image can be downloaded from
 */
data class RemoteCardBrandImage(val type: String, val url: String)

/**
 * [CardDefaults] defines default validation rules for the fields
 *
 * @property pan a default pan validation rule
 * @property cvv a default cvv validation rule
 * @property month a default month validation rule
 * @property year a default year validation rule
 */
data class CardDefaults(
    val pan: CardValidationRule,
    val cvv: CardValidationRule,
    val month: CardValidationRule,
    val year: CardValidationRule
)

/**
 * [CardValidationRule] stores a set of validation rule attributes
 *
 * @property matcher a regex which is used to match a field to this rule
 * @property validLengths a rule which defines the fields valid lengths
 */
data class CardValidationRule(val matcher: String, val validLengths: List<Int>)
