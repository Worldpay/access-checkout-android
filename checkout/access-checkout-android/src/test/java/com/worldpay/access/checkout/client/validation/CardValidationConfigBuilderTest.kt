package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CardValidationConfigBuilderTest {

    private val pan = mock<EditText>()
    private val expiryMonth = mock<EditText>()
    private val expiryYear = mock<EditText>()
    private val cvv = mock<EditText>()
    private val baseUrl = "http://localhost"
    private val validationListener = mock<AccessCheckoutCardValidationSuccessListener>()

    @Test
    fun `should build card validation config`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .validationListener(validationListener)
            .build()

        assertNotNull(config)
        assertEquals(baseUrl, config.baseUrl)
        assertEquals(pan, config.pan)
        assertEquals(expiryMonth, config.expiryMonth)
        assertEquals(expiryYear, config.expiryYear)
        assertEquals(cvv, config.cvv)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where base url is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .pan(pan)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .cvv(cvv)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected base url to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where pan is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .cvv(cvv)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected pan component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where expiry month is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryYear(expiryYear)
                .cvv(cvv)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected expiry month component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where expiry year is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryMonth(expiryMonth)
                .cvv(cvv)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected expiry year component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where cvv is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected cvv component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where validation listener is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .cvv(cvv)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }

}