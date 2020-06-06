package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DINERS_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.JCB_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MAESTRO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MASTERCARD_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CVV_RULE
import com.worldpay.access.checkout.testutils.CardNumberUtil
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Test

class CVVValidatorTest {

    private var cvvValidator: CVVValidator =
        CVVValidator()

    @Test
    fun `given a empty cvv and non-null card configuration then should be only partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a non-empty cvv, and an empty pan and card configuration then should be completely and partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a non-empty alphanumeric cvv, and an empty pan and card configuration then should be completely and partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), null), cvvValidator.validate(
            "A",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a non-empty cvv and pan and an empty card configuration then should be completely and partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            "44",
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv less than defaults min length then should be partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv equal to defaults min length then should be partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv greater than defaults max length then should be completely and partially invalid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), null), cvvValidator.validate(
            "12345",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv less than defaults max length then should be completely invalid and partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv equal to defaults max length then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), null), cvvValidator.validate(
            "1234",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with valid length then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), VISA_BRAND), cvvValidator.validate(
            "123",
            VISA_PAN,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with less than valid length then should be partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), VISA_BRAND), cvvValidator.validate(
            "1",
            PARTIAL_VISA,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with more than valid length then should be completely and partially invalid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), VISA_BRAND), cvvValidator.validate(
            "12345",
            PARTIAL_VISA,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a cvv with length on lower bounds in defaults min and max length then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv with length on upper bounds in defaults min and max length then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), null), cvvValidator.validate(
            "1234",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv with length less than lower bounds in defaults min and max length then should be partially`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a cvv with length more than upper bounds in defaults min and max length then should not be valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), null), cvvValidator.validate(
            "12345",
            null,
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length on upper bounds in brands cvv rule then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), VISA_BRAND), cvvValidator.validate(
            "123",
            PARTIAL_VISA,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length less than lower bounds in brands cvv rule then should be partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), VISA_BRAND), cvvValidator.validate(
            "12",
            PARTIAL_VISA,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that matches a brand rule and a cvv with length more than upper bounds in brands cvv rule then should not be valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), VISA_BRAND), cvvValidator.validate(
            "1234567",
            PARTIAL_VISA,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with valid length against the default rule then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), null), cvvValidator.validate(
            "1234",
            null,
            CARD_CONFIG_BASIC
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            null,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with less than valid length against the default rule then should be partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            "123456",
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with more than valid length against the default rule then should not be valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), null), cvvValidator.validate(
            "12345",
            "123456",
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length equal to lower bounds of default cvv rule then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            "123456",
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length equal to upper bounds of default cvv rule then should be completely valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), null), cvvValidator.validate(
            "1234",
            "123456",
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length less than lower bounds of default cvv rule then should be partially valid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            "123456",
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `given a pan that doesn't match a brand rule and a cvv with length more than upper bounds of default cvv rule then should be invalid`() {
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = false
            ), null), cvvValidator.validate(
            "123456789",
            "123456",
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `should validate cvv based on default rules when pan is null`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            null,
            CARD_CONFIG_BASIC
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), null), cvvValidator.validate(
            "1234",
            null,
            CARD_CONFIG_BASIC
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            null,
            CARD_CONFIG_BASIC
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "1",
            null,
            CARD_CONFIG_BASIC
        ))
    }

    @Test
    fun `should validate cvv based on default rules when pan is empty`() {
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null), cvvValidator.validate(
            "123",
            "",
            CARD_CONFIG_NO_BRAND
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = false,
                complete = true
            ), null), cvvValidator.validate(
            "1234",
            "",
            CARD_CONFIG_NO_BRAND
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "12",
            "",
            CARD_CONFIG_NO_BRAND
        ))
        assertEquals(Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null), cvvValidator.validate(
            "1",
            "",
            CARD_CONFIG_NO_BRAND
        ))
    }

    @Test
    fun `should return branded validation rule when correct pan is used`() {
        assertEquals(VISA_BRAND.cvv, cvvValidator.getValidationRule(VISA_PAN, CARD_CONFIG_BASIC))
        assertEquals(MASTERCARD_BRAND.cvv, cvvValidator.getValidationRule(MASTERCARD_PAN, CARD_CONFIG_BASIC))
        assertEquals(AMEX_BRAND.cvv, cvvValidator.getValidationRule(AMEX_PAN, CARD_CONFIG_BASIC))
        assertEquals(JCB_BRAND.cvv, cvvValidator.getValidationRule(CardNumberUtil.JCB_PAN, CARD_CONFIG_BASIC))
        assertEquals(DISCOVER_BRAND.cvv, cvvValidator.getValidationRule(DISCOVER_PAN, CARD_CONFIG_BASIC))
        assertEquals(DINERS_BRAND.cvv, cvvValidator.getValidationRule(DINERS_PAN, CARD_CONFIG_BASIC))
        assertEquals(MAESTRO_BRAND.cvv, cvvValidator.getValidationRule(MAESTRO_PAN, CARD_CONFIG_BASIC))
    }

    @Test
    fun `should return default validation rule when pan is empty`() {
        assertEquals(CVV_RULE, cvvValidator.getValidationRule("", CARD_CONFIG_BASIC))
        assertEquals(CVV_RULE, cvvValidator.getValidationRule("", CARD_CONFIG_NO_BRAND))
    }

    @Test
    fun `should return default validation rule when pan is is not empty and using default card config`() {
        assertEquals(CVV_RULE, cvvValidator.getValidationRule(VISA_PAN, CARD_CONFIG_NO_BRAND))
    }

}