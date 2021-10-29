package com.worldpay.access.checkout.sample.card.standard

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.CVC
import com.worldpay.access.checkout.sample.testutil.UITestUtils.reopenApp
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardFragmentTest : AbstractCardFragmentTest() {

    @Test
    fun shouldDisplayAllExpectedElementsInCorrectEnabledState() {
        cardFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(pan = true, cvc = true, expiryDate = true, submitButton = false)
    }

    @Test
    fun shouldEnableSubmitButton_onValidCardData() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData() {
        // invalid expiry month
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "6040")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "06/04")
            .focusOn(CVC)
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = false)
            .enabledStateIs(submitButton = false)

        // partial pan number
        cardFragmentTestUtils
            .enterCardDetails(pan = "411111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 111", cvc = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // invalid cvc
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvc = "12", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "12", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData_afterValidDataIsAltered() {
        // enter correct card data
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // enter incorrect card data
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "6040")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "06/04")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = false)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldKeepValidationStateOnFieldsWhenAppIsReopened() {
        restartApp()

        try {
            cardFragmentTestUtils
                .isInInitialState()
                .enterCardDetails(pan = "4", cvc = "12", expiryDate = "129")
                .focusOn(CVC)
                .validationStateIs(pan = false, cvc = false, expiryDate = false)
                .hasBrand(VISA)
                .enabledStateIs(submitButton = false)

            reopenApp()

            cardFragmentTestUtils
                .validationStateIs(pan = false, cvc = false, expiryDate = false)
                .hasBrand(VISA)
                .enabledStateIs(submitButton = false)
        } finally {
            restartApp()
        }
    }
}
