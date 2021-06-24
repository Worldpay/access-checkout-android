package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test

class CardValidationConfigBuilderTest {

    private val pan = mock<EditText>()
    private val expiryDate = mock<EditText>()
    private val cvc = mock<EditText>()
    private val acceptedCardBrands = arrayOf("AMEX", "DINERS", "DISCOVER", "JCB", "MAESTRO", "MASTERCARD", "VISA")
    private val baseUrl = "https://localhost:8443"
    private val validationListener = mock<AccessCheckoutCardValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()

    @Test
    fun `should build card validation config with pan formatting disabled by default`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .acceptedCardBrands(acceptedCardBrands)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(config)
        assertEquals(baseUrl, config.baseUrl)
        assertEquals(pan, config.pan)
        assertEquals(expiryDate, config.expiryDate)
        assertEquals(cvc, config.cvc)
        assertEquals(acceptedCardBrands, config.acceptedCardBrands)
        assertEquals(validationListener, config.validationListener)
        assertFalse(config.enablePanFormatting)
    }

    @Test
    fun `should not throw any exceptions if accepted card brands is not called in the builder and defaults to empty array`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(config)
        assertEquals(baseUrl, config.baseUrl)
        assertEquals(pan, config.pan)
        assertEquals(expiryDate, config.expiryDate)
        assertEquals(cvc, config.cvc)
        assertEquals(0, config.acceptedCardBrands.size)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where base url is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .pan(pan)
                .expiryDate(expiryDate)
                .cvc(cvc)
                .acceptedCardBrands(acceptedCardBrands)
                .validationListener(validationListener)
                .lifecycleOwner(lifecycleOwner)
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
                .cvc(cvc)
                .acceptedCardBrands(acceptedCardBrands)
                .validationListener(validationListener)
                .lifecycleOwner(lifecycleOwner)
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
                .cvc(cvc)
                .acceptedCardBrands(acceptedCardBrands)
                .validationListener(validationListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected expiry date component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where cvc is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryDate(expiryDate)
                .acceptedCardBrands(acceptedCardBrands)
                .validationListener(validationListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected cvc component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where validation listener is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryDate(expiryDate)
                .cvc(cvc)
                .acceptedCardBrands(acceptedCardBrands)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where lifecycle owner is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .baseUrl(baseUrl)
                .pan(pan)
                .expiryDate(expiryDate)
                .cvc(cvc)
                .acceptedCardBrands(acceptedCardBrands)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected lifecycle owner to be provided but was not", exception.message)
    }

    @Test
    fun `should be able to call enable pan number formatting`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .acceptedCardBrands(acceptedCardBrands)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .enablePanFormatting()
            .build()

        assertNotNull(config)
        assertEquals(baseUrl, config.baseUrl)
        assertEquals(pan, config.pan)
        assertEquals(expiryDate, config.expiryDate)
        assertEquals(cvc, config.cvc)
        assertEquals(acceptedCardBrands, config.acceptedCardBrands)
        assertEquals(validationListener, config.validationListener)
        assertTrue(config.enablePanFormatting)
    }
}
