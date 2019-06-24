package com.worldpay.access.checkout.config

import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
import org.json.JSONObject
import java.io.InputStream

internal class CardConfigurationParser : Deserializer<CardConfiguration>() {

    companion object {
        //CardConfiguration fields
        private const val DEFAULTS_FIELD = "defaults"
        private const val BRANDS_FIELD = "brands"

        //Defaults fields
        private const val PAN_FIELD = "pan"
        private const val CVV_FIELD = "cvv"
        private const val MONTH_FIELD = "month"
        private const val YEAR_FIELD = "year"

        // Brand fields
        private const val NAME_FIELD = "name"
        private const val IMAGE_FIELD = "image"
        private const val BRANDED_CVV_FIELD = "cvv"
        private const val PANS_FIELD = "pans"

        // Card validation rule fields
        private const val MATCHER_FIELD = "matcher"
        private const val MIN_LENGTH_FIELD = "minLength"
        private const val MAX_LENGTH_FIELD = "maxLength"
        private const val VALID_LENGTH_FIELD = "validLength"
        private const val SUB_RULES_FIELD = "subRules"
    }

    fun parse(cardConfiguration: InputStream?): CardConfiguration {
        return cardConfiguration?.let {
            val json = it.reader(Charsets.UTF_8).readText()
            return if (json.isBlank()) CardConfiguration()
            else deserialize(json)
        } ?: CardConfiguration()
    }

    override fun deserialize(json: String): CardConfiguration {
        return super.deserialize(json) {
            val root = JSONObject(json)
            CardConfiguration(parseBrandsConfig(root), parseDefaultConfig(root))
        }
    }

    private fun parseBrandsConfig(root: JSONObject): List<CardBrand>? {
        val brandsArray = fetchOptionalArray(root, BRANDS_FIELD)
        return brandsArray?.let {
            val brandsList = mutableListOf<CardBrand>()
            for (i in 0 until it.length()) {
                val brandRoot = it.getJSONObject(i)
                val name = toStringProperty(brandRoot, NAME_FIELD)
                val image = toStringProperty(brandRoot, IMAGE_FIELD)
                val cvv = fetchObject(brandRoot, BRANDED_CVV_FIELD)
                val cvvConfig = parseCardValidationRule(cvv)
                val pans = fetchArray(brandRoot, PANS_FIELD)
                val panList = mutableListOf<CardValidationRule>()
                for (panRuleIndex in 0 until pans.length()) {
                    val panValidationRule = parseCardValidationRule(pans.getJSONObject(panRuleIndex))
                    panList.add(panValidationRule)
                }
                brandsList.add(CardBrand(name, image, cvvConfig, panList))
            }
            brandsList
        }
    }

    private fun parseDefaultConfig(jsonObject: JSONObject): CardDefaults? {
        val defaults = fetchOptionalObject(jsonObject, DEFAULTS_FIELD)
        return defaults?.let {
            val pan = fetchOptionalObject(it, PAN_FIELD)
            val cvv = fetchOptionalObject(it, CVV_FIELD)
            val month = fetchOptionalObject(it, MONTH_FIELD)
            val year = fetchOptionalObject(it, YEAR_FIELD)
            val panConfig = parseOptionalCardValidationRule(pan)
            val cvvConfig = parseOptionalCardValidationRule(cvv)
            val monthConfig = parseOptionalCardValidationRule(month)
            val yearConfig = parseOptionalCardValidationRule(year)
            CardDefaults(panConfig, cvvConfig, monthConfig, yearConfig)
        }
    }

    private fun parseCardValidationRule(jsonObject: JSONObject): CardValidationRule {
        val matcher = toOptionalStringProperty(jsonObject, MATCHER_FIELD)
        val minLength = toOptionalIntProperty(jsonObject, MIN_LENGTH_FIELD)
        val maxLength = toOptionalIntProperty(jsonObject, MAX_LENGTH_FIELD)
        val validLength = toOptionalIntProperty(jsonObject, VALID_LENGTH_FIELD)
        val subRulesList = parseSubRules(jsonObject)
        return CardValidationRule(matcher, minLength, maxLength, validLength, subRulesList)
    }

    private fun parseOptionalCardValidationRule(jsonObject: JSONObject?): CardValidationRule? {
        return jsonObject?.let { parseCardValidationRule(it) }
    }

    private fun parseSubRules(jsonObject: JSONObject): List<CardValidationRule> {
        val subRules = fetchOptionalArray(jsonObject, SUB_RULES_FIELD)
        val subRulesList = mutableListOf<CardValidationRule>()
        subRules?.let {
            for (i in 0 until it.length()) {
                val cardValidationRule = parseCardValidationRule(it.getJSONObject(i))
                subRulesList.add(cardValidationRule)
            }
        }
        return subRulesList
    }
}
