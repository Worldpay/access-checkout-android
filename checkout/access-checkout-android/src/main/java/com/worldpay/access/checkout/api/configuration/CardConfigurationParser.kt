package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.model.*
import org.json.JSONArray
import org.json.JSONObject
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

        // Default card rules
        private val panDefaults:CardValidationRule = CardValidationRule(null, listOf(15,16,18,19))
        private val cvvDefaults:CardValidationRule = CardValidationRule(null, listOf(3,4))
        private val monthDefaults:CardValidationRule = CardValidationRule("^0[1-9]{0,1}$|^1[0-2]{0,1}$", listOf(2))
        private val yearDefaults:CardValidationRule = CardValidationRule("^\\d{0,2}$", listOf(2))
        private val cardDefaults:CardDefaults = CardDefaults(
            panDefaults,
            cvvDefaults,
            monthDefaults,
            yearDefaults
        )
    }

    fun parse(cardConfiguration: InputStream?): CardConfiguration {
        return cardConfiguration?.let {
            val json = it.reader(Charsets.UTF_8).readText()
            return if (json.isBlank()) CardConfiguration(defaults = cardDefaults)
            else deserialize(json)
        } ?: CardConfiguration(defaults = cardDefaults)
    }

    override fun deserialize(json: String): CardConfiguration {
        return super.deserialize(json) {
            val root = JSONArray(json)
            CardConfiguration(parseBrandsConfig(root), cardDefaults)
        }
    }

    private fun parseBrandsConfig(root: JSONArray): List<CardBrand>? {
        return root?.let {
            val brandsList = mutableListOf<CardBrand>()
            for (i in 0 until it.length()) {
                val brandRoot = it.getJSONObject(i)
                val name = toStringProperty(brandRoot,
                    NAME_FIELD
                )
                val images = fetchOptionalArray(brandRoot,
                    IMAGES_FIELD
                )
                val brandImages = parseBrandImages(images)
                val cvv = toOptionalIntProperty(brandRoot, BRANDED_CVV_FIELD)
                val cvvConfig = cvv?.let { CardValidationRule(null, listOf(it)) }
                val pans = fetchOptionalArray(brandRoot, PANS_FIELD)
                val matcher = toOptionalStringProperty(brandRoot, MATCHER_FIELD)
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

    private fun parseLengths(jsonArray: JSONArray): List<Int>? {
        val validLengthsList = mutableListOf<Int>()
        jsonArray.let {
            for (i in 0 until it.length()) {
                validLengthsList.add(it[i] as Int)
            }
        }
        return validLengthsList
    }
}
