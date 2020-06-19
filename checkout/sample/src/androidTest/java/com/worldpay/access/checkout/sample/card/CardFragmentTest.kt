package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardFragmentTest: AbstractCardFragmentTest() {

    @Test
    fun shouldDisplayAllExpectedElementsInCorrectEnabledState() {
        cardFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(pan = true, cvv = true, expiryDate = true, submitButton = false)
    }

    @Test
    fun shouldEnableSubmitButton_onValidCardData() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvv = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData() {
        // invalid expiry month
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvv = "123", expiryDate = "6040")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", expiryDate = "06/04")
            .focusOn(CardFragmentTestUtils.Input.CVV)
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, expiryDate = false)
            .enabledStateIs(submitButton = false)

        // partial pan number
        cardFragmentTestUtils
            .enterCardDetails(pan = "411111111111111", cvv = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "411111111111111", cvv = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = false, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // invalid cvv
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvv = "12", expiryDate = "1140")
            .cardDetailsAre(pan = "4111111111111111", cvv = "12", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = false, expiryDate = true)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData_afterValidDataIsAltered() {
        // enter correct card data
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvv = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // enter incorrect card data
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvv = "123", expiryDate = "6040")
            .cardDetailsAre(pan = "4111111111111111", cvv = "123", expiryDate = "06/04")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvv = true, expiryDate = false)
            .enabledStateIs(submitButton = false)
    }

}
