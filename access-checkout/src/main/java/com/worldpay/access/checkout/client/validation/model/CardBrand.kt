package com.worldpay.access.checkout.client.validation.model

/**
 * This enum represents all supported card brands by Access Checkout
 */
enum class CardBrands {
    AMEX,
    DINERS,
    DISCOVER,
    JCB,
    MAESTRO,
    MASTERCARD,
    VISA
}

/**
 * This class represents a Card brand that can be found for an associated pan
 *
 * @property[name] The [String] property that represents the name of the card brand
 * @property[images] The [List] of [CardBrandImage] property that represents the images associated to that brand i.e. logo
 */
data class CardBrand(val name: String, val images: List<CardBrandImage>)

/**
 * This class represents a Card brand image that is associated to a [CardBrand]
 *
 * @property[type] The [String] property that represents the image type
 * @property[url] The [String] property that points to the image resource path
 */
data class CardBrandImage(val type: String, val url: String)
