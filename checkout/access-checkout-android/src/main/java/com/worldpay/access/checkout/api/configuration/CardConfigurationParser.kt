package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.DEFAULT_MATCHER
import com.worldpay.access.checkout.api.serialization.Deserializer
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

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

    fun parse(cardConfiguration: InputStream?): CardConfiguration {
        return cardConfiguration?.let {
            val json = it.reader(Charsets.UTF_8).readText()
            return if (json.isBlank()) CardConfiguration(emptyList(), CARD_DEFAULTS)
            else deserialize(json)
        } ?: CardConfiguration(emptyList(), CARD_DEFAULTS)
    }

    override fun deserialize(json: String): CardConfiguration {
        return super.deserialize(json) {
            val root = JSONArray(json)
            CardConfiguration(parseBrandsConfig(root), CARD_DEFAULTS)
        }
    }

    private fun parseBrandsConfig(root: JSONArray): List<CardBrand> {
        return root.let {
            val brandsList = mutableListOf<CardBrand>()
            for (i in 0 until it.length()) {
                val brandRoot = it.getJSONObject(i)

                val cardBrand = CardBrand(
                    name = toStringProperty(brandRoot, BRAND_NAME_FIELD),
                    images = getBrandImages(brandRoot),
                    cvv = getCvvRule(brandRoot),
                    pan = getPanRule(brandRoot)
                )

                brandsList.add(cardBrand)
            }
            brandsList
        }
    }

    private fun getBrandImages(brandRoot: JSONObject): List<CardBrandImage> {
        val images = fetchOptionalArray(brandRoot, BRAND_IMAGES_FIELD) ?: return emptyList()

        val brandImageList = mutableListOf<CardBrandImage>()
        for (brandImageIndex in 0 until images.length()) {
            val brandImage = images.getJSONObject(brandImageIndex)
            val type = toStringProperty(brandImage, CARD_IMAGE_TYPE_FIELD)
            val url = toStringProperty(brandImage, CARD_IMAGE_URL_FIELD)
            brandImageList.add(CardBrandImage(type, url))
        }

        return brandImageList
    }

    private fun getCvvRule(brandRoot: JSONObject): CardValidationRule {
        val cvvLength = toOptionalProperty(brandRoot, BRAND_CVV_LENGTH_FIELD, Int::class) ?: return CVV_DEFAULTS
        return CardValidationRule(matcher = DEFAULT_MATCHER, validLengths = listOf(cvvLength))
    }

    private fun getPanRule(brandRoot: JSONObject): CardValidationRule {
        val pans = fetchOptionalArray(brandRoot, BRAND_PAN_LENGTHS_FIELD)
        val matcher = toOptionalProperty(brandRoot, MATCHER_FIELD, String::class) ?: DEFAULT_MATCHER
        return CardValidationRule(matcher, parseLengths(pans as JSONArray))
    }

    private fun parseLengths(jsonArray: JSONArray): List<Int> {
        val validLengthsList = mutableListOf<Int>()
        jsonArray.let {
            for (i in 0 until it.length()) {
                validLengthsList.add(it[i] as Int)
            }
        }
        return validLengthsList
    }

}
