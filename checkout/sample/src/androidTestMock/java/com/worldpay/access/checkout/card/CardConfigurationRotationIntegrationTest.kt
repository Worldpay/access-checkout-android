package com.worldpay.access.checkout.card

import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.card.testutil.AbstractCardFlowUITest
import com.worldpay.access.checkout.card.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.testutil.UITestUtils.setOrientationLeft
import com.worldpay.access.checkout.testutil.UITestUtils.setOrientationNatural
import org.junit.Test

class CardConfigurationRotationIntegrationTest: AbstractCardFlowUITest() {

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "12"
    private val month = "12"
    private val year = "99"

    fun activity(): MainActivity = activityRule.activity

    @Test
    fun givenScreenIsRotated_ThenFieldsShouldKeepValidationState() {
        // Enter an invalid luhn, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .isInInitialState()
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvv = unknownCvv, month = "13", year = year)
            .validationStateIs(pan = false, cvv = false, month = false, year = false)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)

        // rotate landscape
        setOrientationLeft()

        // Re-enter a luhn valid, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .validationStateIs(pan = false, cvv = false, month = false, year = false)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "123", month = month, year = year)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)

        // rotate portrait
        setOrientationNatural()

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "12", month = month, year = year)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)

        // rotate landscape
        setOrientationLeft()

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvv = false, month = true, year = true)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "123", month = month, year = year)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
    }

}