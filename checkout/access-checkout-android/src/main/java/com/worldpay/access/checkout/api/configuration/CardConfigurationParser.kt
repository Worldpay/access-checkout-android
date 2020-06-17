package com.worldpay.access.checkout.api.configuration

import android.util.Log
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.DEFAULT_MATCHER
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.PAN_DEFAULTS
import com.worldpay.access.checkout.api.serialization.Deserializer
import org.json.JSONArray
import org.json.JSONObject

internal class CardConfigurationParser : Deserializer<CardConfiguration>() {

    internal companion object {

        private const val EMPTY_STRING = ""

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
        return try {
            super.deserialize(json) {
                val brands = parseBrandsConfig(JSONArray(json))
                CardConfiguration(brands, CARD_DEFAULTS)
            }
        } catch (e: Exception) {
            Log.w(javaClass.simpleName, e.message, e)
            CardConfiguration(emptyList(), CARD_DEFAULTS)
        }
    }

    private fun parseBrandsConfig(root: JSONArray): List<RemoteCardBrand> {
        val brandsList = mutableListOf<RemoteCardBrand>()

        for (i in 0 until root.length()) {
            val brand = root.getJSONObject(i)

            val cardBrand = RemoteCardBrand(
                name = this.toStringProperty(brand, BRAND_NAME_FIELD, EMPTY_STRING),
                images = getBrandImages(brand),
                cvv = getCvvRule(brand),
                pan = getPanRule(brand)
            )

            brandsList.add(cardBrand)
        }

        return brandsList
    }

    private fun getBrandImages(brand: JSONObject): List<RemoteCardBrandImage> {
        val images = fetchOptionalArray(brand, BRAND_IMAGES_FIELD) ?: return emptyList()

        val brandImageList = mutableListOf<RemoteCardBrandImage>()

        for (brandImageIndex in 0 until images.length()) {
            val brandImage = getBrandImageObject(images, brandImageIndex) ?: continue

            val type = this.toStringProperty(brandImage, CARD_IMAGE_TYPE_FIELD, EMPTY_STRING)
            val url = this.toStringProperty(brandImage, CARD_IMAGE_URL_FIELD, EMPTY_STRING)

            brandImageList.add(RemoteCardBrandImage(type, url))
        }

        return brandImageList
    }

    private fun getBrandImageObject(images: JSONArray, index: Int): JSONObject? {
        return try {
            images.getJSONObject(index)
        } catch (e: Exception) {
            Log.w(javaClass.simpleName, e.message, e)
            null
        }
    }

    private fun getCvvRule(brand: JSONObject): CardValidationRule {
        var validLengths = CVV_DEFAULTS.validLengths

        try {
            val cvvLength = toOptionalProperty(brand, BRAND_CVV_LENGTH_FIELD, Int::class)
            if (cvvLength != null) {
                validLengths = listOf(cvvLength)
            }
        } catch (e: Exception) {
            Log.w(javaClass.simpleName, e.message, e)
        }

        return CardValidationRule(matcher = DEFAULT_MATCHER, validLengths = validLengths)
    }

    private fun getPanRule(brand: JSONObject): CardValidationRule {
        var validLengths = PAN_DEFAULTS.validLengths

        try {
            val panLengths = fetchOptionalArray(brand, BRAND_PAN_LENGTHS_FIELD)
            if (panLengths != null) {
                validLengths = parseLengths(panLengths)
            }
        } catch (e: Exception) {
            Log.w(javaClass.simpleName, e.message, e)
        }

        val matcher = this.toStringProperty(brand, MATCHER_FIELD, DEFAULT_MATCHER)
        return CardValidationRule(matcher, validLengths)
    }

    private fun toStringProperty(obj: JSONObject, field: String, defaultValue: String): String {
        return try {
            super.toStringProperty(obj, field)
        } catch (e: Exception) {
            Log.w(javaClass.simpleName, e.message, e)
            defaultValue
        }
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
