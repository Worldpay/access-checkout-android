package com.worldpay.access.checkout.card

import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.card.testutil.AbstractCardFlowUITest
import com.worldpay.access.checkout.card.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.testutil.UITestUtils.rotateToLandscapeAndWait
import com.worldpay.access.checkout.testutil.UITestUtils.rotateToPortraitAndWait
import org.junit.Test

class CardConfigurationRotationIntegrationTest: AbstractCardFlowUITest() {

    private val timeoutInMillis = 3000L

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "12"
    private val month = "12"
    private val year = "99"

    fun activity(): MainActivity = activityRule.activity

    @Test
    fun givenScreenIsRotated_ThenFieldsShouldKeepValidationState() {
        cardFragmentTestUtils.isInInitialState()

        // Enter an invalid luhn, mastercard identified card and valid date
        cardFragmentTestUtils
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvv = unknownCvv, month = "13", year = year)
            .validationStateIs(pan = false, cvv = false, month = false, year = false)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)

        //rotate and assert state is the same
        rotateToLandscapeAndWait(activity(), timeoutInMillis) {
            cardFragmentTestUtils
                .validationStateIs(pan = false, cvv = false, month = false, year = false)
                .hasBrand(MASTERCARD)
                .enabledStateIs(submitButton = false)
            true
        }

        // new activity created due to rotation
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule.activity)

        // Re-enter a luhn valid, mastercard identified card and valid date
        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "123", month = month, year = year)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)

        rotateToPortraitAndWait(activity(), timeoutInMillis) {
            cardFragmentTestUtils
                .hasBrand(MASTERCARD)
                .validationStateIs(pan = true, cvv = true, month = true, year = true)
                .enabledStateIs(submitButton = true)
            true
        }

        // new activity created due to rotation
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule.activity)

        // Verify that all the fields are now in a success state and can be submitted
        cardFragmentTestUtils
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
            .enterCardDetails(cvv = "12")
            .enabledStateIs(submitButton = false)

        //rotate and assert state is the same
        rotateToLandscapeAndWait(activity(), timeoutInMillis) {
            cardFragmentTestUtils
                .hasBrand(MASTERCARD)
                .validationStateIs(pan = true, cvv = false, month = true, year = true)
                .enabledStateIs(submitButton = false)
            true
        }

        // new activity created due to rotation
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule.activity)

        cardFragmentTestUtils
            .enabledStateIs(submitButton = false)
            .enterCardDetails(cvv = "123")
            .enabledStateIs(submitButton = true)
    }

}