package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NewPanValidatorTest {

    private val panValidator = NewPanValidator()

    @Test
    fun `should return false and no Card Brand if Pan is empty`() {
        val result = panValidator.validate("",
            CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
        )
        assertFalse(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and null card brand if pan is unrecognised and shorter than default valid lengths`() {
        val result = panValidator.validate("11111",
            CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
        )

        assertFalse(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and if unkown pan is valid length and an invalid luhn`() {
        val result = panValidator.validate("1111111111111111",
            CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
        )

        assertFalse(result.first)
        assertNull(result.second)

    }

    @Test
    fun `should return true and if unrecognised pan is valid luhn and valid length`() {
        val result = panValidator.validate("8888888888888888",
            CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
        )

        assertTrue(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and card brand if known pan is shorter than valid length`() {
        val result = panValidator.validate("4111",
            CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertEquals(result.second?.name, "visa")
    }

    @Test
    fun `should return false and card brand if known pan is valid length but invalid luhn`() {
        val result = panValidator.validate("44444444444444444",
            CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertEquals(result.second?.name, "visa")
    }

    @Test
    fun `should return true and card brand if known pan is valid length and valid luhn`() {
        val result = panValidator.validate("4111111111111111",
            CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
        )

        assertTrue(result.first)
        assertEquals(result.second?.name, "visa")
    }

    @Test
    fun `should recognise new brand when updated from visa to maestro`() {
        var result = panValidator.validate("49369",
            CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
        )

        assertEquals(result.second?.name, "visa")

        result = panValidator.validate("493698",
            CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
        )

        assertEquals(result.second?.name, "maestro")
    }
}
