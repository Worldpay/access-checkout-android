package com.worldpay.access.checkout.sample.card

import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotateLandscape
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Test

class CardConfigurationRotationIntegrationTest: AbstractCardFragmentTest() {

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "12"

    fun activity(): MainActivity = activityRule.activity

    @Test
    fun givenScreenIsRotated_ThenFieldsShouldKeepValidationState() {
        // Enter an invalid luhn, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .isInInitialState()
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvv = unknownCvv, expiryDate = "0119")
            .validationStateIs(pan = false, cvv = false, expiryDate = false)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)

        // rotate landscape
        rotateLandscape(activityRule)

        // Re-enter a luhn valid, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .validationStateIs(pan = false, cvv = false, expiryDate = false)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "123", expiryDate = "1299")
            .cardDetailsAre(pan = luhnValidMastercardCard, cvv = "123", expiryDate = "12/99")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // rotate portrait
        rotatePortrait(activityRule)

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "12", expiryDate = "1299")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // rotate landscape
        rotateLandscape(activityRule)

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = false, expiryDate = true)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "123", expiryDate = "1299")
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

}
