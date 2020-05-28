package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DINERS_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.JCB_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MAESTRO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MASTERCARD_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnInvalidUnknownCardSize19
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnValidUnknownCardSize19
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnValidVisaCardSize16
import com.worldpay.access.checkout.validation.CardRulesTestFactory.visaCard
import org.junit.Assert.*
import org.junit.Test

class PANValidatorTest {

    private val panValidator = PANValidator()

    // validatePAN

    @Test
    fun `given empty configuration then pan validator should not be null`() {
        assertNotNull(PANValidator())
    }

    @Test
    fun `given card number entered and a bad matcher for card defaults then should be completely invalid`() {
        val result = panValidator.validate("abc", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given incomplete card number entered and using default matcher then should be partially valid but completely invalid`() {
        val result = panValidator.validate("8839", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given partial luhn-invalid card entered for card defaults then should be partially valid`() {
        val result = panValidator.validate("1234", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given full luhn-invalid card entered for card defaults then should be partially valid`() {
        val result = panValidator.validate("4111111111111112", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and only card defaults present then should be partially valid`() {
        val result = panValidator.validate("", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and only brand rules present then should be partially valid`() {
        val result = panValidator.validate("", CARD_CONFIG_BASIC)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and both card defaults and brand rules present then should be partially valid`() {
        val result = panValidator.validate("", CARD_CONFIG_BASIC)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card is entered then should be completely invalid`() {
        val result = panValidator.validate(visaCard + "A", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card with special chars is entered then should be completely invalid`() {
        val result = panValidator.validate(visaCard + "_*", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card with mix of digits and non-numeric chars is entered then should be completely invalid`() {
        val result = panValidator.validate(visaCard + "_AB", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given too short card entered for matcher then should be partially valid and completely invalid`() {
        val result = panValidator.validate("1234", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given too long card entered for matcher then should be completely invalid`() {
        val result = panValidator.validate("02184074752863339921527185", CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid card with correct size for lower bound of matcher should be completely and partially valid`() {
        val result = panValidator.validate(luhnValidVisaCardSize16,  CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid card with correct size for upper bound of matcher should be completely and partially valid`() {
        val result = panValidator.validate(luhnValidVisaCardSize16, CARD_CONFIG_NO_BRAND)

        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given luhn-invalid identified cards and multiple brand rules config then should only be partially valid`() {
        val result1 = panValidator.validate("4", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertEquals(VISA_BRAND, result1.second)

        val result2 = panValidator.validate("51", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = true, complete = false), result2.first)
        assertEquals(MASTERCARD_BRAND, result2.second)
    }

    @Test
    fun `given luhn-valid identified cards and multiple brand rules config then should be completely valid`() {
        val result1 = panValidator.validate(VISA_PAN, CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = true, complete = true), result1.first)
        assertEquals(VISA_BRAND, result1.second)

        val result2 = panValidator.validate(AMEX_PAN, CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = false, complete = true), result2.first)
        assertEquals(AMEX_BRAND, result2.second)
    }

    @Test
    fun `given luhn-invalid unidentified card should only be partially valid`() {
        val result = panValidator.validate("6", CARD_CONFIG_BASIC)

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given luhn-valid unidentified card should only be completely valid`() {
        val result = panValidator.validate("8464222316187751443", CARD_CONFIG_BASIC)

        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given amex card then should be completely valid`() {
        val result = panValidator.validate(AMEX_PAN, CARD_CONFIG_BASIC)

        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertEquals(result.second, AMEX_BRAND)
    }

    @Test
    fun `given luhn-valid pans then should be completely valid`() {
        val pans = listOf(
            "4111111111111111", // Visa
            "5000111122223336", // Mastercard
            "340011112222332", // Amex
            "370011112222335", // Amex
            "0000000000000000",
            "0000000000000000000"
        )

        val panValidator = PANValidator()

        pans.forEach {
            val result = panValidator.validate(it, CARD_CONFIG_NO_BRAND)
            assertEquals(ValidationResult(partial = it.length != 19, complete = true), result.first)
            assertNull(result.second)
        }
    }

    @Test
    fun `given luhn-invalid pan then should be partially valid`() {
        val panValidator = PANValidator()

        val result = panValidator.validate("456756789654", CARD_CONFIG_NO_BRAND)
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid identified card with correct size should be completely invalid`() {
        val panValidator = PANValidator()

        val result = panValidator.validate(luhnInvalidUnknownCardSize19, CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertEquals(null, result.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with varying lengths then should validate against min and max rule`() {
        val result1 = panValidator.validate("8464222316", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertNull(result1.second)

        val result2 = panValidator.validate("8464222316187751443", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = false, complete = true), result2.first)
        assertNull(result2.second)

        val result3 = panValidator.validate("02184074752863339921527185", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = false, complete = false), result3.first)
        assertNull(result3.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with correct matcher for the default rule then should be completely valid`() {
        val result = panValidator.validate("0000000000000000", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with min length size should be partially and completely valid`() {
        val result = panValidator.validate("9364473081789059", CARD_CONFIG_BASIC)
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with max length size should be completely valid`() {
        val result = panValidator.validate(luhnValidUnknownCardSize19, CARD_CONFIG_NO_BRAND)
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with more than max size should be completely invalid`() {
        val result = panValidator.validate("02184074752863339921527185", CARD_CONFIG_NO_BRAND)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid unidentified card should be completely invalid`() {
        val result = panValidator.validate(luhnInvalidUnknownCardSize19, CARD_CONFIG_NO_BRAND)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return branded validation rule when correct pan is used`() {
        assertEquals(VISA_BRAND.pan, panValidator.getValidationRule(VISA_PAN, CARD_CONFIG_BASIC))
        assertEquals(MASTERCARD_BRAND.pan, panValidator.getValidationRule(MASTERCARD_PAN, CARD_CONFIG_BASIC))
        assertEquals(AMEX_BRAND.pan, panValidator.getValidationRule(AMEX_PAN, CARD_CONFIG_BASIC))
        assertEquals(JCB_BRAND.pan, panValidator.getValidationRule(JCB_PAN, CARD_CONFIG_BASIC))
        assertEquals(DISCOVER_BRAND.pan, panValidator.getValidationRule(DISCOVER_PAN, CARD_CONFIG_BASIC))
        assertEquals(DINERS_BRAND.pan, panValidator.getValidationRule(DINERS_PAN, CARD_CONFIG_BASIC))
        assertEquals(MAESTRO_BRAND.pan, panValidator.getValidationRule(MAESTRO_PAN, CARD_CONFIG_BASIC))
    }

    @Test
    fun `should return default validation rule when pan is empty`() {
        assertEquals(PAN_RULE, panValidator.getValidationRule("", CARD_CONFIG_BASIC))
        assertEquals(PAN_RULE, panValidator.getValidationRule("", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `should return default validation rule when pan is is not empty and using default card config`() {
        assertEquals(PAN_RULE, panValidator.getValidationRule(VISA_PAN, CARD_CONFIG_NO_BRAND))
    }

}