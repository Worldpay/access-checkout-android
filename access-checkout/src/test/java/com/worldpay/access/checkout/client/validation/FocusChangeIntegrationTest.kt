package com.worldpay.access.checkout.client.validation

import android.os.Looper.getMainLooper
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrandList
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import kotlin.test.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class FocusChangeIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - pan`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))

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
        verifyNoInteractions(cardValidationListener)

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
        verifyNoInteractions(cardValidationListener)

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
        verifyNoInteractions(cardValidationListener)

        expiryDate.requestFocus()

        if (expiryDate.hasFocus()) {
            expiryDate.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onExpiryDateValidated(false)
    }
}
