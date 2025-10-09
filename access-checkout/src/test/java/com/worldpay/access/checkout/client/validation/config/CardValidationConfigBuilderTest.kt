package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlin.test.*
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.mock

class CardValidationConfigBuilderTest {

    private val pan = mock<AccessCheckoutEditText>()
    private val expiryDate = mock<AccessCheckoutEditText>()
    private val cvc = mock<AccessCheckoutEditText>()

    private val panInternalEditText = mock<EditText>()
    private val expiryDateInternalEditText = mock<EditText>()
    private val cvcInternalEditText = mock<EditText>()

    private val acceptedCardBrands =
        arrayOf("AMEX", "DINERS", "DISCOVER", "JCB", "MAESTRO", "MASTERCARD", "VISA")
    private val validationListener = mock<AccessCheckoutCardValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()

    @Before
    fun setUp() {
        given(pan.editText).willReturn(panInternalEditText)
        given(expiryDate.editText).willReturn(expiryDateInternalEditText)
        given(cvc.editText).willReturn(cvcInternalEditText)
    }

    @Test
    fun `should build card validation config with pan formatting disabled by default`() {
        val config = CardValidationConfig.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .acceptedCardBrands(acceptedCardBrands)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(config)
        assertEquals(panInternalEditText, config.pan)
        assertEquals(expiryDateInternalEditText, config.expiryDate)
        assertEquals(cvcInternalEditText, config.cvc)
        assertEquals(acceptedCardBrands, config.acceptedCardBrands)
        assertEquals(validationListener, config.validationListener)
        assertFalse(config.enablePanFormatting)
    }

    @Test
    fun `should not throw any exceptions if accepted card brands is not called in the builder and defaults to empty array`() {
        val config = CardValidationConfig.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(config)
        assertEquals(panInternalEditText, config.pan)
        assertEquals(expiryDateInternalEditText, config.expiryDate)
        assertEquals(cvcInternalEditText, config.cvc)
        assertEquals(0, config.acceptedCardBrands.size)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where pan is not provided`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            CardValidationConfig.Builder()
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
        val exception = assertFailsWith<AccessCheckoutException> {
            CardValidationConfig.Builder()
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
        val exception = assertFailsWith<AccessCheckoutException> {
            CardValidationConfig.Builder()
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
        val exception = assertFailsWith<AccessCheckoutException> {
            CardValidationConfig.Builder()
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
        val exception = assertFailsWith<AccessCheckoutException> {
            CardValidationConfig.Builder()
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
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .acceptedCardBrands(acceptedCardBrands)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .enablePanFormatting()
            .build()

        assertNotNull(config)
        assertEquals(panInternalEditText, config.pan)
        assertEquals(expiryDateInternalEditText, config.expiryDate)
        assertEquals(cvcInternalEditText, config.cvc)
        assertEquals(acceptedCardBrands, config.acceptedCardBrands)
        assertEquals(validationListener, config.validationListener)
        assertTrue(config.enablePanFormatting)
    }
}
