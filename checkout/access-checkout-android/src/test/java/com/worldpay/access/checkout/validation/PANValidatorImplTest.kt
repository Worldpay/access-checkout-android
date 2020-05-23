package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CARD_DEFAULTS
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnInvalidUnknownCardSize19
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnInvalidVisaCardSize16
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnValidUnknownCardSize19
import com.worldpay.access.checkout.validation.CardRulesTestFactory.luhnValidVisaCardSize16
import com.worldpay.access.checkout.validation.CardRulesTestFactory.visaCard
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PANValidatorImplTest {

    private lateinit var panValidator: PANValidator

    @Before
    fun setup() {
        panValidator = PANValidatorImpl(null)
    }

    // validatePAN

    @Test
    fun `given empty configuration then pan validator should not be null`() {
        assertNotNull(PANValidatorImpl(null))
    }

    @Test
    fun `given no card number entered and no configuration then should be completely valid`() {
        val result = panValidator.validate("")

        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given non-numeric card number entered and no configuration then should be partially valid`() {
        val result = panValidator.validate("ABC")

        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given card number entered and a bad matcher for card defaults then should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("abc")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given incomplete card number entered and using default matcher then should be partially valid but completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("8839")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given partial luhn-invalid card entered for card defaults then should be partially valid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("1234")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given full luhn-invalid card entered for card defaults then should be partially valid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("4111111111111112")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and only card defaults present then should be partially valid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and only brand rules present then should be partially valid`() {
        val brandRule =
            CardValidationRule(
                "^4\\d{0,15}$",
                listOf(16)
            )
        val cardBrand = CardBrand(
            "some brand",
            emptyList(),
            null,
            brandRule
        )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand),
                defaults = CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and both card defaults and brand rules present then should be partially valid`() {
        val brandRule =
            CardValidationRule(
                "^4\\d{0,15}$",
                listOf(16)
            )
        val cardBrand = CardBrand(
            "some brand",
            emptyList(),
            null,
            brandRule
        )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card is entered then should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(visaCard + "A")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card with special chars is entered then should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(visaCard + "_*")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card with mix of digits and non-numeric chars is entered then should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(visaCard + "_AB")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given too short card entered for matcher then should be partially valid and completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("1234")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given too long card entered for matcher then should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("02184074752863339921527185")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid card with correct size for lower bound of matcher should be completely and partially valid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(luhnValidVisaCardSize16)
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid card with correct size for upper bound of matcher should be completely and partially valid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(luhnValidVisaCardSize16)
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid identified card and single brand rules config then should only be partially valid`() {
        val brandRule =
            CardValidationRule(
                "^\\d{0,4}$",
                emptyList()
            )
        val cardBrand = CardBrand(
            "some brand",
            emptyList(),
            null,
            brandRule
        )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand),
                defaults = CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("123")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertEquals(cardBrand, result.second)
    }

    @Test
    fun `given luhn-invalid identified cards and multiple brand rules config then should only be partially valid`() {
        val brandRule1 =
            CardValidationRule(
                "^4\\d{0,4}$",
                emptyList()
            )
        val brandRule2 =
            CardValidationRule(
                "^5\\d{0,4}$",
                emptyList()
            )
        val cardBrand1 =
            CardBrand(
                "some brand 1",
                emptyList(),
                null,
                brandRule1
            )
        val cardBrand2 =
            CardBrand(
                "some brand 2",
                emptyList(),
                null,
                brandRule2
            )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand1, cardBrand2),
                defaults = CARD_DEFAULTS
            )
        )

        val result1 = panValidator.validate("4")
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertEquals(cardBrand1, result1.second)

        val result2 = panValidator.validate("5")
        assertEquals(ValidationResult(partial = true, complete = false), result2.first)
        assertEquals(cardBrand2, result2.second)
    }

    @Test
    fun `given luhn-valid identified cards and multiple brand rules config then should be completely valid`() {
        val brandRule1 =
            CardValidationRule(
                "^9\\d{0,3}$",
                emptyList()
            )
        val brandRule2 =
            CardValidationRule(
                "^8\\d{0,3}$",
                emptyList()
            )
        val cardBrand1 =
            CardBrand(
                "some brand 1",
                emptyList(),
                null,
                brandRule1
            )
        val cardBrand2 =
            CardBrand(
                "some brand 2",
                emptyList(),
                null,
                brandRule2
            )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand1, cardBrand2),
                defaults = CARD_DEFAULTS
            )
        )

        val result1 = panValidator.validate("9159")
        assertEquals(ValidationResult(partial = true, complete = true), result1.first)
        assertEquals(cardBrand1, result1.second)

        val result2 = panValidator.validate("8573")
        assertEquals(ValidationResult(partial = true, complete = true), result2.first)
        assertEquals(cardBrand2, result2.second)
    }

    @Test
    fun `given luhn-invalid unidentified card should only be partially valid`() {
        val panValidator = PANValidatorImpl(CARD_CONFIG_BASIC)

        val result = panValidator.validate("6")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given luhn-valid unidentified card should only be completely valid`() {
        val panValidator = PANValidatorImpl(CARD_CONFIG_BASIC)

        val result = panValidator.validate(luhnValidUnknownCardSize19)
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given amex card then should be completely valid`() {
        val panValidator = PANValidatorImpl(CARD_CONFIG_BASIC)

        val result = panValidator.validate(AMEX_PAN)
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertEquals(result.second, AMEX_BRAND)
    }

    @Test
    fun `given amex card too long to match for brand validation rule and no defaults then should be completely valid`() {
        val panValidator = PANValidatorImpl(CARD_CONFIG_BASIC)

        val result = panValidator.validate("3400000000000091")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
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

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                defaults = CARD_DEFAULTS
            )
        )

        pans.forEach {
            val result = panValidator.validate(it)
            assertEquals(ValidationResult(partial = it.length != 19, complete = true), result.first)
            assertNull(result.second)
        }
    }

    @Test
    fun `given luhn-invalid pan then should be partially valid`() {
        val panValidator = PANValidatorImpl(CARD_CONFIG_NO_BRAND)

        val result = panValidator.validate("456756789654")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid identified card with correct size should be completely invalid`() {
        val brandRule =
            CardValidationRule(
                "^4\\d{0,15}",
                listOf(16)
            )
        val cardBrand = CardBrand(
            "some brand",
            emptyList(),
            null,
            brandRule
        )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(luhnInvalidVisaCardSize16)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertEquals(cardBrand, result.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with varying lengths then should validate against min and max rule`() {
        val brandRule1 =
            CardValidationRule(
                "^2\\d{0,4}$",
                emptyList()
            )
        val brandRule2 =
            CardValidationRule(
                "^4\\d{0,4}$",
                emptyList()
            )
        val cardBrand1 =
            CardBrand(
                "some brand 1",
                emptyList(),
                null,
                brandRule1
            )
        val cardBrand2 =
            CardBrand(
                "some brand 2",
                emptyList(),
                null,
                brandRule2
            )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand1, cardBrand2),
                CARD_DEFAULTS
            )
        )

        val result1 = panValidator.validate("272099309")
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertNull(result1.second)

        val result2 = panValidator.validate("2720993096669388")
        assertEquals(ValidationResult(partial = true, complete = true), result2.first)
        assertNull(result2.second)

        val result3 = panValidator.validate("02184074752863339921527185")
        assertEquals(ValidationResult(partial = false, complete = false), result3.first)
        assertNull(result3.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with correct matcher for the default rule then should be completely valid`() {
        val brandRule1 =
            CardValidationRule(
                "^2\\d{0,4}$",
                emptyList()
            )
        val brandRule2 =
            CardValidationRule(
                "^4\\d{0,4}$",
                emptyList()
            )
        val cardBrand1 =
            CardBrand(
                "some brand 1",
                emptyList(),
                null,
                brandRule1
            )
        val cardBrand2 =
            CardBrand(
                "some brand 2",
                emptyList(),
                null,
                brandRule2
            )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand1, cardBrand2),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("2720993096669388")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid identified card with less than valid length then should be partially valid`() {
        val brandRule =
            CardValidationRule(
                "^4\\d{0,15}",
                listOf(17)
            )
        val cardBrand = CardBrand(
            "some brand",
            emptyList(),
            null,
            brandRule
        )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand),
                defaults = CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(luhnValidVisaCardSize16)
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertEquals(cardBrand, result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with min length size should be partially and completely valid`() {
        val brandRule =
            CardValidationRule(
                "^4\\d{0,15}",
                listOf(16)
            )
        val cardBrand = CardBrand(
            "some brand",
            emptyList(),
            null,
            brandRule
        )

        val panValidator = PANValidatorImpl(
            CardConfiguration(
                listOf(cardBrand),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("2720993096669388")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with max length size should be completely valid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(luhnValidUnknownCardSize19)
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with more than max size should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate("02184074752863339921527185")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid unidentified card should be completely invalid`() {
        val panValidator = PANValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        val result = panValidator.validate(luhnInvalidUnknownCardSize19)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }
}