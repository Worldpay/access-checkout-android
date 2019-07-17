package com.worldpay.access.checkout.model

/**
 * [CardBrandImage] stores the reference to an image resource
 * for a particular [CardBrand]. You can use the url reference
 * to the [CardBrand] to fetch it and apply it to the UI when the
 * brand has been identified
 *
 * @param type the media type of the image, e.g. 'image/svg+xml'
 * @param url the URL to where the image can be downloaded from
 */
data class CardBrandImage(val type: String, val url: String)
