package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.DEFAULT_MATCHER
import com.worldpay.access.checkout.api.serialization.Deserializer
import org.json.JSONArray
import java.io.InputStream

internal class CardConfigurationParser : Deserializer<CardConfiguration>() {

    companion object {

        // Brand fields
        private const val NAME_FIELD = "name"

        private const val IMAGES_FIELD = "images"
        private const val CARD_IMAGE_TYPE_FIELD = "type"
        private const val CARD_IMAGE_URL_FIELD = "url"
        private const val BRANDED_CVV_FIELD = "cvvLength"
        private const val PANS_FIELD = "panLengths"

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

                // Parse brand name
                val name = toStringProperty(brandRoot, NAME_FIELD)

                // Parse images
                val images = fetchOptionalArray(brandRoot, IMAGES_FIELD)
                val brandImages = parseBrandImages(images)

                // Parse cvv
                val cvv = toOptionalProperty(brandRoot, BRANDED_CVV_FIELD, Int::class)
                val cvvConfig = cvv?.let { cvvLength ->
                    CardValidationRule(matcher = DEFAULT_MATCHER, validLengths = listOf(cvvLength))
                }

                // Parse PAN rule
                val pans = fetchOptionalArray(brandRoot, PANS_FIELD)
                val matcher = toOptionalProperty(brandRoot, MATCHER_FIELD, String::class) ?: DEFAULT_MATCHER
                val panRule = CardValidationRule(matcher, parseLengths(pans as JSONArray))

                brandsList.add(CardBrand(name, brandImages, cvvConfig, panRule))
            }
            brandsList
        }
    }

    private fun parseBrandImages(images: JSONArray?): List<CardBrandImage> {
        return images?.let {
            val brandImageList = mutableListOf<CardBrandImage>()
            for (brandImageIndex in 0 until images.length()) {
                val brandImage = images.getJSONObject(brandImageIndex)
                val type = toStringProperty(brandImage,
                    CARD_IMAGE_TYPE_FIELD
                )
                val url = toStringProperty(brandImage,
                    CARD_IMAGE_URL_FIELD
                )
                brandImageList.add(CardBrandImage(type, url))
            }
            brandImageList
        } ?: emptyList()
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
