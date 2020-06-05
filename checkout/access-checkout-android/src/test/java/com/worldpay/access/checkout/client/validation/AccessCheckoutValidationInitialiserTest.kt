package com.worldpay.access.checkout.client.validation

import android.text.TextWatcher
import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.ArgumentMatchers.any

class AccessCheckoutValidationInitialiserTest {

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    private val baseUrl = "http://localhost"
    private val pan: EditText = mock()
    private val expiryMonth: EditText = mock()
    private val expiryYear: EditText = mock()
    private val cvv: EditText = mock()
    private val cardValidationListener: AccessCheckoutCardValidationListener = mock()
    private val cvvValidationListener: AccessCheckoutCvvValidationListener = mock()

    @Test
    fun `should be able to initialise the validation for card validation`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryMonth(expiryMonth)
            .expiryYear(expiryYear)
            .cvv(cvv)
            .validationListener(cardValidationListener)
            .build()

        AccessCheckoutValidationInitialiser.initialise(config)

        verify(pan).addTextChangedListener(any(TextWatcher::class.java))
        verify(expiryMonth).addTextChangedListener(any(TextWatcher::class.java))
        verify(expiryYear).addTextChangedListener(any(TextWatcher::class.java))
        verify(cvv).addTextChangedListener(any(TextWatcher::class.java))
    }

    @Test
    fun `should be able to initialise the validation for cvv validation`() {
        val config = CvvValidationConfig.Builder()
            .cvv(cvv)
            .validationListener(cvvValidationListener)
            .build()

        AccessCheckoutValidationInitialiser.initialise(config)

        verify(cvv).addTextChangedListener(any(TextWatcher::class.java))
    }

}