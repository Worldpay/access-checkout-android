package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NewPANValidatorTest {

    private val panValidator = NewPANValidator()

    @Test
    fun `should return false and no Card Brand if Pan is empty`() {
        val result = panValidator.validate("",
            CARD_CONFIG_BASIC
        )
        assertFalse(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and null card brand if pan is unrecognised and shorter than default valid lengths`() {
        val result = panValidator.validate("11111",
            CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and null card brand if pan has invalid characters`() {
        val result = panValidator.validate("aaaaaaaaaaaaaaaa",
            CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and no card brand if unkown pan is valid length and an invalid luhn`() {
        val result = panValidator.validate("1111111111111111",
            CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertNull(result.second)

    }

    @Test
    fun `should return true and if unrecognised pan is valid luhn and valid length`() {
        val result = panValidator.validate("8888888888888888",
            CARD_CONFIG_BASIC
        )

        assertTrue(result.first)
        assertNull(result.second)
    }

    @Test
    fun `should return false and card brand if known pan is shorter than valid length`() {
        val result = panValidator.validate("4111",
            CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertEquals(result.second?.name, "visa")
    }

    @Test
    fun `should return false and card brand if known pan is valid length but invalid luhn`() {
        val result = panValidator.validate("44444444444444444",
            CARD_CONFIG_BASIC
        )

        assertFalse(result.first)
        assertEquals(result.second?.name, "visa")
    }

    @Test
    fun `should return true and card brand if known pan is valid length and valid luhn`() {
        val result = panValidator.validate("4111111111111111",
            CARD_CONFIG_BASIC
        )

        assertTrue(result.first)
        assertEquals(result.second?.name, "visa")
    }

    @Test
    fun `should recognise new brand when updated from visa to maestro`() {
        var result = panValidator.validate("49369",
            CARD_CONFIG_BASIC
        )

        assertEquals(result.second?.name, "visa")

        result = panValidator.validate("493698",
            CARD_CONFIG_BASIC
        )

        assertEquals(result.second?.name, "maestro")
    }
}
