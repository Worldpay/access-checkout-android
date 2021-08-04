package com.worldpay.access.checkout.client.validation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlin.test.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FocusChangeIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - pan`() {
        pan.setText(visaPan())
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))

        pan.requestFocus()

        if (pan.hasFocus()) {
            pan.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verifyNoMoreInteractions(cardValidationListener)
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - cvc`() {
        cvc.setText("123")
        verify(cardValidationListener).onCvcValidated(true)

        cvc.requestFocus()

        if (cvc.hasFocus()) {
            cvc.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verifyNoMoreInteractions(cardValidationListener)
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - expiry date`() {
        expiryDate.setText("12/99")
        verify(cardValidationListener).onExpiryDateValidated(true)

        expiryDate.requestFocus()

        if (expiryDate.hasFocus()) {
            expiryDate.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verifyNoMoreInteractions(cardValidationListener)
    }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - pan`() {
        pan.setText("0000")
        verifyZeroInteractions(cardValidationListener)

        pan.requestFocus()

        if (pan.hasFocus()) {
            pan.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onPanValidated(false)
    }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - cvc`() {
        cvc.setText("")
        verifyZeroInteractions(cardValidationListener)

        cvc.requestFocus()

        if (cvc.hasFocus()) {
            cvc.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onCvcValidated(false)
    }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - expiry date`() {
        expiryDate.setText("01/19")
        verifyZeroInteractions(cardValidationListener)

        expiryDate.requestFocus()

        if (expiryDate.hasFocus()) {
            expiryDate.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onExpiryDateValidated(false)
    }
}
