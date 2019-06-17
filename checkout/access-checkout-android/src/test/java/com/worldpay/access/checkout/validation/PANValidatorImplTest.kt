package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
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

    private val panDefaults = CardValidationRule("^\\d{0,19}$", 13, 19, null)

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
        val cardValidationRule = CardValidationRule("", null, null, null)
        val cardDefaults = CardDefaults(cardValidationRule, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), cardDefaults))

        val result = panValidator.validate("1234")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given card number entered and no matcher for card defaults then should not be partially valid but be completely valid`() {
        val cardValidationRule = CardValidationRule(null, null, null, 4)
        val cardDefaults = CardDefaults(cardValidationRule, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), cardDefaults))

        val result = panValidator.validate("8839")
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given partial luhn-invalid card entered for card defaults then should be partially valid`() {
        val panDefaults = CardValidationRule("^\\d{0,19}$", null, null, 16)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), defaults))

        val result = panValidator.validate("1234")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given full luhn-invalid card entered for card defaults then should be partially valid`() {
        val panDefaults = CardValidationRule("^\\d{0,16}$", null, 16, null)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), defaults))

        val result = panValidator.validate("4111111111111112")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and only card defaults present then should be partially valid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(null, defaults))

        val result = panValidator.validate("")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and only brand rules present then should be partially valid`() {
        val brandRule = CardValidationRule("^4\\d{0,15}$", null, null, 16)
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand)))

        val result = panValidator.validate("")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given no card number entered and both card defaults and brand rules present then should be partially valid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)
        val brandRule = CardValidationRule("^4\\d{0,15}$", null, null, 16)
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        val result = panValidator.validate("")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card is entered then should be completely invalid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(), defaults))

        val result = panValidator.validate(visaCard + "A")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card with special chars is entered then should be completely invalid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(), defaults))

        val result = panValidator.validate(visaCard + "_*")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a non-numeric card with mix of digits and non-numeric chars is entered then should be completely invalid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(), defaults))

        val result = panValidator.validate(visaCard + "_AB")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given too short card entered for matcher then should be completely invalid`() {
        val panDefaults = CardValidationRule("^\\d{5,19}$", null, null, null)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), defaults))

        val result = panValidator.validate("1234")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given too long card entered for matcher then should be completely invalid`() {
        val panDefaults = CardValidationRule("^\\d{0,3}$", null, null, null)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), defaults))

        val result = panValidator.validate("1234")
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid card with correct size for lower bound of matcher should be completely and partially valid`() {
        val panDefaults = CardValidationRule("^\\d{16,19}$", null, null, null)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), defaults))

        val result = panValidator.validate(luhnValidVisaCardSize16)
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid card with correct size for upper bound of matcher should be completely and partially valid`() {
        val panDefaults = CardValidationRule("^\\d{0,16}$", null, null, null)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList(), defaults))

        val result = panValidator.validate(luhnValidVisaCardSize16)
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given card brand with no pan rule then should be completely valid`() {
        val cardBrand = CardBrand("some brand", "", null, emptyList())

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand)))

        val result = panValidator.validate("8839")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid identified card and single brand rules config then should only be partially valid`() {
        val brandRule = CardValidationRule("^\\d{0,4}$", null, null, null)
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand)))

        val result = panValidator.validate("123")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertEquals(cardBrand, result.second)
    }

    @Test
    fun `given luhn-invalid identified cards and multiple brand rules config then should only be partially valid`() {
        val brandRule1 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^5\\d{0,4}$", null, null, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2)))

        val result1 = panValidator.validate("4")
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertEquals(cardBrand1, result1.second)

        val result2 = panValidator.validate("5")
        assertEquals(ValidationResult(partial = true, complete = false), result2.first)
        assertEquals(cardBrand2, result2.second)
    }

    @Test
    fun `given luhn-valid identified cards and multiple brand rules config then should be completely valid`() {
        val brandRule1 = CardValidationRule("^9\\d{0,3}$", null, null, null)
        val brandRule2 = CardValidationRule("^8\\d{0,3}$", null, null, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2)))

        val result1 = panValidator.validate("9159")
        assertEquals(ValidationResult(partial = true, complete = true), result1.first)
        assertEquals(cardBrand1, result1.second)

        val result2 = panValidator.validate("8573")
        assertEquals(ValidationResult(partial = true, complete = true), result2.first)
        assertEquals(cardBrand2, result2.second)
    }

    @Test
    fun `given luhn-invalid unidentified card should only be partially valid`() {
        val brandRule1 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^5\\d{0,4}$", null, null, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2)))

        val result = panValidator.validate("6")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given luhn-valid unidentified card should only be completely valid`() {
        val brandRule1 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^5\\d{0,4}$", null, null, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2)))

        val result = panValidator.validate("6111111111111111118")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given amex card then should be completely valid`() {
        val amexRule = CardValidationRule("^3[47]\\d{0,13}$", null, null, null)
        val amex = CardBrand("amex", "", null, listOf(amexRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(amex)))

        val result = panValidator.validate("340000000000009")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertEquals(result.second, amex)
    }

    @Test
    fun `given amex card too long to match for brand validation rule and no defaults then should be completely valid`() {
        val amexRule = CardValidationRule("^3[47]\\d{0,13}$", null, null, 15)
        val amex = CardBrand("amex", "", null, listOf(amexRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(amex)))

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

        val panValidator = PANValidatorImpl(CardConfiguration(emptyList()))

        pans.forEach {
            val result = panValidator.validate(it)
            assertEquals(ValidationResult(partial = true, complete = true), result.first)
            assertNull(result.second)
        }
    }

    @Test
    fun `given luhn-invalid pan then should be partially valid`() {
        val panValidator = PANValidatorImpl(CardConfiguration(emptyList()))

        val result = panValidator.validate("456756789654")
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid identified card with correct size should be completely invalid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)
        val brandRule = CardValidationRule("^4\\d{0,15}", null, null, 16)
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        val result = panValidator.validate(luhnInvalidVisaCardSize16)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertEquals(cardBrand, result.second)
    }

    @Test
    fun `given a card brand which has sub-rules then should be able to validate successfully`() {
        val defaults = CardDefaults(null, null, null, null)
        val subRule = CardValidationRule("^413600\\d{0,7}", null, null, 13)
        val brandRule = CardValidationRule("^4\\d{0,15}", null, null, 16, listOf(subRule))
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        // sub-rule match - 13 chars
        val result1 = panValidator.validate("4136001111119")
        assertEquals(ValidationResult(partial = false, complete = true), result1.first)
        assertEquals(cardBrand, result1.second)

        // sub-rule match - 14 chars (invalid length)
        val result2 = panValidator.validate("41360011111191")
        assertEquals(ValidationResult(partial = false, complete = false), result2.first)
        assertEquals(cardBrand, result2.second)

        // sub-rule match - 12 chars (invalid length)
        val result3 = panValidator.validate("413600111111")
        assertEquals(ValidationResult(partial = true, complete = false), result3.first)
        assertEquals(cardBrand, result3.second)

        // main rule match - luhn valid - 16 chars
        val result4 = panValidator.validate("4136011111111114")
        assertEquals(ValidationResult(partial = false, complete = true), result4.first)
        assertEquals(cardBrand, result4.second)

        // main rule match - partially valid - 15 chars
        val result5 = panValidator.validate("411111111111111")
        assertEquals(ValidationResult(partial = true, complete = false), result5.first)
        assertEquals(cardBrand, result5.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with varying lengths then should validate against valid length rule`() {
        val brandRule1 = CardValidationRule("^2\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val defaultRule = CardValidationRule(null, null, null, 5)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2), CardDefaults(defaultRule, null, null, null)))

        val result1 = panValidator.validate("8123")
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertNull(result1.second)

        val result2 = panValidator.validate("00703")
        assertEquals(ValidationResult(partial = false, complete = true), result2.first)
        assertNull(result2.second)

        val result3 = panValidator.validate("523597")
        assertEquals(ValidationResult(partial = false, complete = false), result3.first)
        assertNull(result3.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with varying lengths then should validate against min and max rule`() {
        val brandRule1 = CardValidationRule("^2\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val defaultRule = CardValidationRule(null, 3, 5, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2), CardDefaults(defaultRule, null, null, null)))

        val result1 = panValidator.validate("59")
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertNull(result1.second)

        val result2 = panValidator.validate("729")
        assertEquals(ValidationResult(partial = false, complete = true), result2.first)
        assertNull(result2.second)

        val result3 = panValidator.validate("1172")
        assertEquals(ValidationResult(partial = false, complete = true), result3.first)
        assertNull(result3.second)

        val result4 = panValidator.validate("91314")
        assertEquals(ValidationResult(partial = false, complete = true), result4.first)
        assertNull(result4.second)

        val result5 = panValidator.validate("523597")
        assertEquals(ValidationResult(partial = false, complete = false), result5.first)
        assertNull(result5.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with varying lengths then should validate against min rule only`() {
        val brandRule1 = CardValidationRule("^2\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val defaultRule = CardValidationRule(null, 3, null, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2), CardDefaults(defaultRule, null, null, null)))

        val result1 = panValidator.validate("59")
        assertEquals(ValidationResult(partial = true, complete = false), result1.first)
        assertNull(result1.second)

        val result2 = panValidator.validate("729")
        assertEquals(ValidationResult(partial = false, complete = true), result2.first)
        assertNull(result2.second)

        val result3 = panValidator.validate("1172")
        assertEquals(ValidationResult(partial = false, complete = true), result3.first)
        assertNull(result3.second)
    }

    @Test
    fun `given luhn-valid unidentified  cards with varying lengths then should validate against max rule only`() {
        val brandRule1 = CardValidationRule("^2\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val defaultRule = CardValidationRule(null, null, 3, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2), CardDefaults(defaultRule, null, null, null)))

        val result1 = panValidator.validate("59")
        assertEquals(ValidationResult(partial = true, complete = true), result1.first)
        assertNull(result1.second)

        val result2 = panValidator.validate("729")
        assertEquals(ValidationResult(partial = true, complete = true), result2.first)
        assertNull(result2.second)

        val result3 = panValidator.validate("1172")
        assertEquals(ValidationResult(partial = false, complete = false), result3.first)
        assertNull(result3.second)
    }

    @Test
    fun `given luhn-valid unidentified cards with correct matcher for the default rule then should be completely valid`() {
        val brandRule1 = CardValidationRule("^2\\d{0,4}$", null, null, null)
        val brandRule2 = CardValidationRule("^4\\d{0,4}$", null, null, null)
        val defaultRule = CardValidationRule("^0\\d{0,5}$", null, null, null)
        val cardBrand1 = CardBrand("some brand 1", "", null, listOf(brandRule1))
        val cardBrand2 = CardBrand("some brand 2", "", null, listOf(brandRule2))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand1, cardBrand2), CardDefaults(defaultRule, null, null, null)))

        val result = panValidator.validate("00703")
        assertEquals(ValidationResult(partial = true, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid identified card with less than valid length then should be partially valid`() {
        val brandRule = CardValidationRule("^4\\d{0,15}", null, null, 17)
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand)))

        val result = panValidator.validate(luhnValidVisaCardSize16)
        assertEquals(ValidationResult(partial = true, complete = false), result.first)
        assertEquals(cardBrand, result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with min length size should be completely valid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)
        val brandRule = CardValidationRule("^4\\d{0,15}", null, null, 16)
        val cardBrand = CardBrand("some brand", "", null, listOf(brandRule))

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        val result = panValidator.validate("1111111111112")
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with max length size should be completely valid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(), defaults))

        val result = panValidator.validate(luhnValidUnknownCardSize19)
        assertEquals(ValidationResult(partial = false, complete = true), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-valid unidentified card with more than max size should be completely invalid`() {
        val panDefaults = CardValidationRule("^\\d{0,18}", 13, 18, null)
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(), defaults))

        val result = panValidator.validate(luhnValidUnknownCardSize19)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }

    @Test
    fun `given a luhn-invalid unidentified card should be completely invalid`() {
        val defaults = CardDefaults(panDefaults, null, null, null)

        val panValidator = PANValidatorImpl(CardConfiguration(listOf(), defaults))

        val result = panValidator.validate(luhnInvalidUnknownCardSize19)
        assertEquals(ValidationResult(partial = false, complete = false), result.first)
        assertNull(result.second)
    }
}