package com.worldpay.access.checkout.sample.card.standard

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.*
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.EXPIRY_DATE
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CVCUITests: AbstractCardFragmentTest() {

    private val amex = "343434343434343"
    private val mastercard = "5197278258794613"
    private val visa = "4111111111111111"

    @Test
    fun shouldObserveCvcRules_noBrand() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = "", expiryDate = "1140")
            .hasNoBrand()

        // max 4 digits - so enter 5 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "12345")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "1234")
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // min 3 digits - so enter 2 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "12")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "12")
            .validationStateIs(pan = false, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // accept 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "123")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // accept 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "1234")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "1234")
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldObserveCvcRules_amex() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = amex, expiryDate = "1140")
            .hasBrand(AMEX)

        // max 4 digits - so enter 5 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "12345")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "1234")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // min 4 digits - so enter 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "123")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // accept 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "1234")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "1234")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldObserveCvcRules_mastercard() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = mastercard, expiryDate = "1140")
            .hasBrand(MASTERCARD)

        // max 3 digits - so enter 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "1234")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // min 3 digits - so enter 2 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "12")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "12")
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // accept 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "123")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldObserveCvcRules_visa() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = visa, expiryDate = "1140")
            .hasBrand(VISA)

        // max 3 digits - so enter 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "1234")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // min 3 digits - so enter 2 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "12")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "12")
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // accept 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvc = "123")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldRevalidateCvc_whenCardBrandChanges() {
        // enter amex details, cvc is invalid
        cardFragmentTestUtils
            .enterCardDetails(pan = amex, cvc = "123", expiryDate = "1140")
            .cardDetailsAre(cvc = "123")
            .hasBrand(AMEX)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // change pan to mastercard, cvc is now valid
        cardFragmentTestUtils
            .enterCardDetails(pan = mastercard)
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "123")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldRevalidateCvc_whenPanIsEntered() {
        // enter cvc
        cardFragmentTestUtils
            .isInInitialState()
            .cardDetailsAre(pan = "")
            .hasNoBrand()
            .enterCardDetails(cvc = "1234")
            .focusOn(EXPIRY_DATE)
            .validationStateIs(cvc = true)

        // enter visa card number - cvc is valid
        cardFragmentTestUtils
            .enterCardDetails(pan = visa)
            .focusOn(EXPIRY_DATE)
            .hasBrand(VISA)
            .validationStateIs(cvc = false)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldOnlyKeepMaxCvcLength_whenPasting() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "12345")
            .focusOn(EXPIRY_DATE)
            .cardDetailsAre(cvc = "1234")
            .enabledStateIs(submitButton = false)
    }
}
