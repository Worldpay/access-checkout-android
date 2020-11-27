package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CardValidationConfigBuilderTest {

    private val pan = mock<EditText>()
    private val expiryDate = mock<EditText>()
    private val cvc = mock<EditText>()
    private val baseUrl = "https://localhost:8443"
    private val validationListener = mock<AccessCheckoutCardValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()

    @Test
    fun `should build card validation config`() {
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
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where base url is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardValidationConfig.Builder()
                .pan(pan)
                .expiryDate(expiryDate)
                .cvc(cvc)
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
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected lifecycle owner to be provided but was not", exception.message)
    }

}
