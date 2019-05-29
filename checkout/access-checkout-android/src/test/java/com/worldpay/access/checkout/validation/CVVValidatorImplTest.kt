package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CVVValidatorImplTest {

    private lateinit var cvvValidator: CVVValidator

    @Before
    fun setup() {
       cvvValidator = CVVValidatorImpl(CardConfiguration())
    }

    // validateCVV
    @Test
    fun `given an empty cvv, pan and card configuration then should be completely and partially valid`() {
        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("", null))
    }

    @Test
    fun `given a non-empty cvv, and an empty pan and card configuration then should be completely and partially valid`() {
        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", null))
    }

    @Test
    fun `given a non-empty alphanumeric cvv, and an empty pan and card configuration then should be completely and partially valid`() {
        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("A", null))
    }

    @Test
    fun `given a non-empty cvv and pan and an empty card configuration then should be completely and partially valid`() {
        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", "44"))
    }

    @Test
    fun `given a cvv greater than defaults min length then should be completely and partially valid`() {
        val cvvValidationRule = CardValidationRule(null, 3, null, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("1234", ""))
    }

    @Test
    fun `given a cvv less than defaults min length then should be partially valid`() {
        val cvvValidationRule = CardValidationRule(null, 3, null, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", null))
    }

    @Test
    fun `given a cvv equal to defaults min length then should be partially valid`() {
        val cvvValidationRule = CardValidationRule(null, 3, null, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("123", null))
    }

    @Test
    fun `given a cvv greater than defaults max length then should be completely and partially invalid`() {
        val cvvValidationRule = CardValidationRule(null, null, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidator.validate("12345", null))
    }

    @Test
    fun `given a cvv less than defaults max length then should be completely and partially valid`() {
        val cvvValidationRule = CardValidationRule(null, null, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", null))
    }

    @Test
    fun `given a cvv equal to defaults max length then should be completely and partially valid`() {
        val cvvValidationRule = CardValidationRule(null, null, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("1234", null))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with valid length then should be completely valid`() {
        val cvvRule = CardValidationRule(null, null, null, 4)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), cardBrand), cvvValidatorImpl.validate("1234", "123456"))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with less than valid length then should be partially valid`() {
        val cvvRule = CardValidationRule(null, null, null, 4)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = true, complete = false), cardBrand), cvvValidatorImpl.validate("123", "123456"))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with more than valid length then should be completely and partially invalid`() {
        val cvvRule = CardValidationRule(null, null, null, 4)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = false, complete = false), cardBrand), cvvValidatorImpl.validate("12345", "123456"))
    }

    @Test
    fun `given empty cvv defaults and pan doesn't match against any card brand pan rules then should return completely valid`() {
        val cvvRule = CardValidationRule(null, null, null, 4)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaults = CardDefaults(null, null, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("12345", "123456"))
    }

    @Test
    fun `given no default configuration and non-empty brand configuration and pan doesn't match against any card brand pan rules then should return completely valid`() {
        val cvvRule = CardValidationRule(null, null, null, 4)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidator = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("12345", "123456"))
    }

    @Test
    fun `given a cvv with length on lower bounds in defaults min and max length then should be completely valid`() {
        val cvvValidationRule = CardValidationRule(null, 3, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("123", null))
    }

    @Test
    fun `given a cvv with length on upper bounds in defaults min and max length then should be completely valid`() {
        val cvvValidationRule = CardValidationRule(null, 3, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("1234", null))
    }

    @Test
    fun `given a cvv with length less than lower bounds in defaults min and max length then should be partially`() {
        val cvvValidationRule = CardValidationRule(null, 3, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", null))
    }

    @Test
    fun `given a cvv with length more than upper bounds in defaults min and max length then should not be valid`() {
        val cvvValidationRule = CardValidationRule(null, 3, 4, null)
        val defaults = CardDefaults(null, cvvValidationRule, null, null)

        val cvvValidator = CVVValidatorImpl(CardConfiguration(null, defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidator.validate("12345", null))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length on lower bounds in brands cvv rule then should be completely valid`() {
        val cvvRule = CardValidationRule(null, 5, 6, null)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), cardBrand), cvvValidatorImpl.validate("12345", "123456"))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length on upper bounds in brands cvv rule then should be completely valid`() {
        val cvvRule = CardValidationRule(null, 5, 6, null)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), cardBrand), cvvValidatorImpl.validate("123456", "123456"))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length less than lower bounds in brands cvv rule then should be partially valid`() {
        val cvvRule = CardValidationRule(null, 5, 6, null)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = true, complete = false), cardBrand), cvvValidatorImpl.validate("1234", "123456"))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length more than upper bounds in brands cvv rule then should not be valid`() {
        val cvvRule = CardValidationRule(null, 5, 6, null)
        val panRule = CardValidationRule("^\\d{0,19}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand)))

        assertEquals(Pair(ValidationResult(partial = false, complete = false), cardBrand), cvvValidatorImpl.validate("1234567", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with valid length against the default rule then should be completely valid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, null, null, 4)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidatorImpl.validate("1234", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with less than valid length against the default rule then should be partially valid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, null, null, 4)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidatorImpl.validate("123", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with more than valid length against the default rule then should not be valid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, null, null, 4)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidatorImpl.validate("12345", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length equal to lower bounds of default cvv rule then should be completely valid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, 7, 8, null)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidatorImpl.validate("1234567", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length equal to upper bounds of default cvv rule then should be completely valid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, 7, 8, null)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidatorImpl.validate("12345678", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length less than lower bounds of default cvv rule then should be partially valid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, 7, 8, null)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidatorImpl.validate("123456", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length more than upper bounds of default cvv rule then should be invalid`() {
        val cvvRule = CardValidationRule(null, null, null, 5)
        val panRule = CardValidationRule("^4\\d{0,15}$", null, null, null)
        val cardBrand = CardBrand("visa", "", cvvRule, listOf(panRule))

        val defaultCvvRule = CardValidationRule(null, 7, 8, null)
        val defaults = CardDefaults(null, defaultCvvRule, null, null)
        val cvvValidatorImpl = CVVValidatorImpl(CardConfiguration(listOf(cardBrand), defaults))

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidatorImpl.validate("123456789", "123456"))
    }

}