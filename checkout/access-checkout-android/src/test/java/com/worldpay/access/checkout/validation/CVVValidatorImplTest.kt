package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CARD_DEFAULTS
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CVVValidatorImplTest {

    private lateinit var cvvValidator: CVVValidator

    @Before
    fun setup() {
       cvvValidator = CVVValidatorImpl(null)
    }

    @Test
    fun `given an empty cvv and null card configuration then should be only completely and partially invalid`() {
        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidator.validate("", null))
    }

    @Test
    fun `given a empty cvv and non-null card configuration then should be only partially valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("", null))
    }

    @Test
    fun `given a non-empty pan, empty cvv and null card configuration then should be completely and partially invalid`() {
        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidator.validate("", "44"))
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
    fun `given a cvv less than defaults min length then should be partially valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", null))
    }

    @Test
    fun `given a cvv equal to defaults min length then should be partially valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", null))
    }

    @Test
    fun `given a cvv greater than defaults max length then should be completely and partially invalid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidator.validate("12345", null))
    }

    @Test
    fun `given a cvv less than defaults max length then should be completely invalid and partially valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", null))
    }

    @Test
    fun `given a cvv equal to defaults max length then should be completely valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("1234", null))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with valid length then should be completely valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = true), VISA_BRAND), cvvValidatorImpl.validate("123", VISA_PAN))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with less than valid length then should be partially valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = true, complete = false), VISA_BRAND), cvvValidatorImpl.validate("1", PARTIAL_VISA))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with more than valid length then should be completely and partially invalid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = false), VISA_BRAND), cvvValidatorImpl.validate("12345", PARTIAL_VISA))
    }

    @Test
    fun `given a cvv with length on lower bounds in defaults min and max length then should be completely valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", null))
    }

    @Test
    fun `given a cvv with length on upper bounds in defaults min and max length then should be completely valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("1234", null))
    }

    @Test
    fun `given a cvv with length less than lower bounds in defaults min and max length then should be partially`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", null))
    }

    @Test
    fun `given a cvv with length more than upper bounds in defaults min and max length then should not be valid`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidator.validate("12345", null))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length on upper bounds in brands cvv rule then should be completely valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = true), VISA_BRAND), cvvValidatorImpl.validate("123", PARTIAL_VISA))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length less than lower bounds in brands cvv rule then should be partially valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = true, complete = false), VISA_BRAND), cvvValidatorImpl.validate("12", PARTIAL_VISA))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length more than upper bounds in brands cvv rule then should not be valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = false), VISA_BRAND), cvvValidatorImpl.validate("1234567", PARTIAL_VISA))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with valid length against the default rule then should be completely valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidatorImpl.validate("1234", null))
        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidatorImpl.validate("123", null))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with less than valid length against the default rule then should be partially valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidatorImpl.validate("12", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with more than valid length against the default rule then should not be valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidatorImpl.validate("12345", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length equal to lower bounds of default cvv rule then should be completely valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidatorImpl.validate("123", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length equal to upper bounds of default cvv rule then should be completely valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidatorImpl.validate("1234", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length less than lower bounds of default cvv rule then should be partially valid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidatorImpl.validate("12", "123456"))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length more than upper bounds of default cvv rule then should be invalid`() {
        val cvvValidatorImpl = CVVValidatorImpl(CARD_CONFIG_BASIC)

        assertEquals(Pair(ValidationResult(partial = false, complete = false), null), cvvValidatorImpl.validate("123456789", "123456"))
    }

    @Test
    fun `should validate cvv based on default rules when pan is null`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", null))
        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("1234", null))
        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", null))
        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("1", null))
    }

    @Test
    fun `should validate cvv based on default rules when pan is empty`() {
        val cvvValidator = CVVValidatorImpl(
            CardConfiguration(
                emptyList(),
                CARD_DEFAULTS
            )
        )

        assertEquals(Pair(ValidationResult(partial = true, complete = true), null), cvvValidator.validate("123", ""))
        assertEquals(Pair(ValidationResult(partial = false, complete = true), null), cvvValidator.validate("1234", ""))
        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("12", ""))
        assertEquals(Pair(ValidationResult(partial = true, complete = false), null), cvvValidator.validate("1", ""))
    }

}