package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardBrand.VISA
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardFragmentTest: AbstractCardFragmentTest() {

    @Test
    fun shouldDisplayAllExpectedElementsInCorrectEnabledState() {
        cardFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(pan = true, cvv = true, expiryMonth = true, expiryYear = true, submitButton = false)
    }

    @Test
    fun shouldEnableSubmitButton_onValidCardData() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvv = "123", month = "11", year = "40")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", month = "11", year = "40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData() {
        // invalid expiry month
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvv = "123", month = "60", year = "40")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", month = "60", year = "40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, month = false, year = false)
            .enabledStateIs(submitButton = false)

        // partial pan number
        cardFragmentTestUtils
            .enterCardDetails(pan = "411111111111111", cvv = "123", month = "11", year = "40")
            .cardDetailsAre(pan = "411111111111111", cvv = "123", month = "11", year = "40")
            .hasBrand(VISA)
            .validationStateIs(pan = false, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // invalid cvv
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvv = "12", month = "11", year = "40")
            .cardDetailsAre(pan = "4111111111111111", cvv = "12", month = "11", year = "40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData_afterValidDataIsAltered() {
        // enter correct card data
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvv = "123", month = "11", year = "40")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", month = "11", year = "40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)

        // enter incorrect card data
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvv = "123", month = "60", year = "40")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", month = "60", year = "40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, month = false, year = false)
            .enabledStateIs(submitButton = false)
    }


}