package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardBrand.*
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.YEAR
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CVVUITests: AbstractCardFragmentTest() {

    private val amex = "343434343434343"
    private val mastercard = "5555555555554444"
    private val visa = "4111111111111111"

    @Test
    fun shouldObserveCvvRules_noBrand() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = "", month = "11", year = "40")
            .hasNoBrand()

        // max 4 digits - so enter 5 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "12345")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "1234")
            .validationStateIs(pan = false, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // min 3 digits - so enter 2 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "12")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "12")
            .validationStateIs(pan = false, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // accept 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "123")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = false, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // accept 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "1234")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "1234")
            .validationStateIs(pan = false, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldObserveCvvRules_amex() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = amex, month = "11", year = "40")
            .hasBrand(AMEX)

        // max 4 digits - so enter 5 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "12345")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "1234")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)

        // min 4 digits - so enter 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "123")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // accept 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "1234")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "1234")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldObserveCvvRules_mastercard() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = mastercard, month = "11", year = "40")
            .hasBrand(MASTERCARD)

        // max 3 digits - so enter 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "1234")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)

        // min 3 digits - so enter 2 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "12")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "12")
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // accept 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "123")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldObserveCvvRules_visa() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(pan = visa, month = "11", year = "40")
            .hasBrand(VISA)

        // max 3 digits - so enter 4 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "1234")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)

        // min 3 digits - so enter 2 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "12")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "12")
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // accept 3 digits
        cardFragmentTestUtils
            .enterCardDetails(cvv = "123")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldRevalidateCvv_whenCardBrandChanges() {
        // enter amex details, cvv is invalid
        cardFragmentTestUtils
            .enterCardDetails(pan = amex, cvv = "123", month = "11", year = "40")
            .cardDetailsAre(cvv = "123")
            .hasBrand(AMEX)
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // change pan to mastercard, cvv is now valid
        cardFragmentTestUtils
            .enterCardDetails(pan = mastercard)
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "123")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldRevalidateCvv_whenPanIsEntered() {
        // enter cvv
        cardFragmentTestUtils
            .isInInitialState()
            .cardDetailsAre(pan = "")
            .hasNoBrand()
            .enterCardDetails(cvv = "1234")
            .focusOn(YEAR)
            .validationStateIs(cvv = true)

        // enter visa card number - cvv is valid
        cardFragmentTestUtils
            .enterCardDetails(pan = visa)
            .focusOn(YEAR)
            .hasBrand(VISA)
            .validationStateIs(cvv = false)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldOnlyKeepMaxCvvLength_whenPasting() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "12345")
            .focusOn(YEAR)
            .cardDetailsAre(cvv = "1234")
            .enabledStateIs(submitButton = false)
    }
}
