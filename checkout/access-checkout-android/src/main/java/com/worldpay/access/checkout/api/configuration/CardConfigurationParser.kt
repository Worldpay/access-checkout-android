package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.DEFAULT_MATCHER
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.api.serialization.Deserializer
import org.json.JSONArray
import org.json.JSONObject

internal class CardConfigurationParser : Deserializer<CardConfiguration>() {

    companion object {

        // Brand fields
        private const val BRAND_NAME_FIELD = "name"
        private const val BRAND_IMAGES_FIELD = "images"
        private const val CARD_IMAGE_TYPE_FIELD = "type"
        private const val CARD_IMAGE_URL_FIELD = "url"
        private const val BRAND_CVV_LENGTH_FIELD = "cvvLength"
        private const val BRAND_PAN_LENGTHS_FIELD = "panLengths"

        // Card validation rule fields
        private const val MATCHER_FIELD = "pattern"
    }

    override fun deserialize(json: String): CardConfiguration {
        if (json.isBlank() || json.trim().startsWith("{")) {
            return CardConfiguration(emptyList(), CARD_DEFAULTS)
        }

        return super.deserialize(json) {
            val brands = parseBrandsConfig(JSONArray(json))
            CardConfiguration(brands, CARD_DEFAULTS)
        }
    }

    private fun parseBrandsConfig(root: JSONArray): List<CardBrand> {
        val brandsList = mutableListOf<CardBrand>()

        for (i in 0 until root.length()) {
            val brand = root.getJSONObject(i)

            val cardBrand = CardBrand(
                name = toStringProperty(brand, BRAND_NAME_FIELD),
                images = getBrandImages(brand),
                cvv = getCvvRule(brand),
                pan = getPanRule(brand)
            )

            brandsList.add(cardBrand)
        }

        return brandsList
    }

    private fun getBrandImages(brand: JSONObject): List<CardBrandImage> {
        val images = fetchOptionalArray(brand, BRAND_IMAGES_FIELD) ?: return emptyList()

        val brandImageList = mutableListOf<CardBrandImage>()

        for (brandImageIndex in 0 until images.length()) {
            val brandImage = images.getJSONObject(brandImageIndex)
            val type = toStringProperty(brandImage, CARD_IMAGE_TYPE_FIELD)
            val url = toStringProperty(brandImage, CARD_IMAGE_URL_FIELD)
            brandImageList.add(CardBrandImage(type, url))
        }

        return brandImageList
    }

    private fun getCvvRule(brand: JSONObject): CardValidationRule {
        val cvvLength = toOptionalProperty(brand, BRAND_CVV_LENGTH_FIELD, Int::class)

        var validLengths = CVV_DEFAULTS.validLengths
        if (cvvLength != null) {
            validLengths = listOf(cvvLength)
        }

        return CardValidationRule(matcher = DEFAULT_MATCHER, validLengths = validLengths)
    }

    private fun getPanRule(brand: JSONObject): CardValidationRule {
        val panLengths = fetchOptionalArray(brand, BRAND_PAN_LENGTHS_FIELD)

        var validLengths = PAN_DEFAULTS.validLengths
        if (panLengths != null) {
            validLengths = parseLengths(panLengths)
        }

        val matcher = toOptionalProperty(brand, MATCHER_FIELD, String::class) ?: DEFAULT_MATCHER
        return CardValidationRule(matcher, validLengths)
    }

    private fun parseLengths(jsonArray: JSONArray): List<Int> {
        val validLengthsList = mutableListOf<Int>()
        jsonArray.let {
            for (i in 0 until it.length()) {
                if (it[i] is Int) {
                    validLengthsList.add(it[i] as Int)
                } else {
                    throw AccessCheckoutDeserializationException("Expected property type int but got something else")
                }
            }
        }
        return validLengthsList
    }

}
