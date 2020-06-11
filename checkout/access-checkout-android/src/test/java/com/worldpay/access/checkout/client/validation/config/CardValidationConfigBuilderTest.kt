package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CardValidationConfigBuilderTest {

    private val pan = mock<EditText>()
    private val expiryDate = mock<EditText>()
    private val cvv = mock<EditText>()
    private val baseUrl = "http://localhost"
    private val validationListener = mock<AccessCheckoutCardValidationListener>()

    @Test
    fun `should build card validation config`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryDate(expiryDate)
            .cvv(cvv)
            .validationListener(validationListener)
            .build()

        assertNotNull(config)
        assertEquals(baseUrl, config.baseUrl)
        assertEquals(pan, config.pan)
        assertEquals(expiryDate, config.expiryDate)
        assertEquals(cvv, config.cvv)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where base url is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .pan(pan)
                .expiryDate(expiryDate)
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
                .expiryDate(expiryDate)
                .cvv(cvv)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected pan component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where expiry date is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .cvv(cvv)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected expiry date component to be provided but was not", exception.message)
    }


    @Test
    fun `should throw exception where cvv is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryDate(expiryDate)
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
                .expiryDate(expiryDate)
                .cvv(cvv)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }

}
