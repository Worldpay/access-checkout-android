package com.worldpay.access.checkout.validation

import android.text.TextWatcher
import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationListener
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.ArgumentMatchers.any

class AccessCheckoutValidationInitialiserTest {

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    private val pan: EditText = mock()
    private val expiryMonth: EditText = mock()
    private val expiryYear: EditText = mock()
    private val cvv: EditText = mock()
    private val validationListener: AccessCheckoutValidationListener = mock()

    @Test
    fun `should be able to initialise the validation`() {
        AccessCheckoutValidationInitialiser()
            .pan(pan)
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .baseUrl("localhost")
            .validationListener(validationListener)
            .initialise()

        verify(pan).addTextChangedListener(any(TextWatcher::class.java))
        verify(expiryMonth).addTextChangedListener(any(TextWatcher::class.java))
        verify(expiryYear).addTextChangedListener(any(TextWatcher::class.java))
        verify(cvv).addTextChangedListener(any(TextWatcher::class.java))
    }

    @Test
    fun `should throw exception where pan is null`() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Expected pan component to be provided but was not")

        AccessCheckoutValidationInitialiser()
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .baseUrl("localhost")
            .validationListener(validationListener)
            .initialise()
    }

    @Test
    fun `should throw exception where expiry month is null`() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Expected expiry month component to be provided but was not")

        AccessCheckoutValidationInitialiser()
            .pan(pan)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .baseUrl("localhost")
            .validationListener(validationListener)
            .initialise()
    }

    @Test
    fun `should throw exception where expiry year is null`() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Expected expiry year component to be provided but was not")

        AccessCheckoutValidationInitialiser()
            .pan(pan)
            .expiryMonth(expiryMonth)
            .cvv(cvv)
            .baseUrl("localhost")
            .validationListener(validationListener)
            .initialise()
    }

    @Test
    fun `should throw exception where cvv is null`() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Expected cvv component to be provided but was not")

        AccessCheckoutValidationInitialiser()
            .pan(pan)
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .baseUrl("localhost")
            .validationListener(validationListener)
            .initialise()
    }

    @Test
    fun `should throw exception where base url is null`() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Expected base url to be provided but was not")

        AccessCheckoutValidationInitialiser()
            .pan(pan)
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .validationListener(validationListener)
            .initialise()
    }

    @Test
    fun `should throw exception where validation listener is null`() {
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Expected validation listener to be provided but was not")

        AccessCheckoutValidationInitialiser()
            .pan(pan)
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .baseUrl("localhost")
            .initialise()
    }

}