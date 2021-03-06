package com.worldpay.access.checkout.sample.card

import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN_FORMATTED
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotateLandscape
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Test

class CardConfigurationRotationIntegrationTest : AbstractCardFragmentTest() {

    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvc = "12"

    fun activity(): MainActivity = activityRule.activity

    @Test
    fun givenScreenIsRotated_ThenFieldsShouldKeepValidationState() {
        // Enter an invalid luhn, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .isInInitialState()
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvc = unknownCvc, expiryDate = "1299")
            .validationStateIs(pan = false, cvc = false, expiryDate = true)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)

        rotateLandscape(activityRule)

        // Re-enter a luhn valid, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .validationStateIs(pan = false, cvc = false, expiryDate = true)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = MASTERCARD_PAN, cvc = "123", expiryDate = "1299")
            .cardDetailsAre(pan = MASTERCARD_PAN_FORMATTED, cvc = "123", expiryDate = "12/99")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        rotatePortrait(activityRule)

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .enterCardDetails(pan = MASTERCARD_PAN, cvc = "12", expiryDate = "1299")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        rotateLandscape(activityRule)

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = MASTERCARD_PAN, cvc = "123", expiryDate = "1299")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }
}
