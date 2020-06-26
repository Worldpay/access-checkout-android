package com.worldpay.access.checkout.sample.card

import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardBrand
import com.worldpay.access.checkout.sample.card.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotateLandscape
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Test

class CardConfigurationRotationIntegrationTest: AbstractCardFragmentTest() {

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvc = "12"

    fun activity(): MainActivity = activityRule.activity

    @Test
    fun givenScreenIsRotated_ThenFieldsShouldKeepValidationState() {
        // Enter a partial pan and check the validation state has not been updated
        CardFragmentTestUtils(activityRule)
            .isInInitialState()
            .enterCardDetails(pan = "4111")
            .validationStateIsUnknown(pan = true)
            .hasBrand(CardBrand.VISA)
            .enabledStateIs(submitButton = false)

        rotateLandscape(activityRule)

        // Check no change on rotation
        CardFragmentTestUtils(activityRule)
            .validationStateIsUnknown(pan = true)
            .hasBrand(CardBrand.VISA)
            .enabledStateIs(submitButton = false)

        // Enter a partial expiry date and check the validation state has not been updated
        CardFragmentTestUtils(activityRule)
            .enterCardDetails(expiryDate = "12")
            .validationStateIsUnknown(expiryDate = true)
            .enabledStateIs(submitButton = false)

        rotatePortrait(activityRule)

        // Check no change on rotation
        CardFragmentTestUtils(activityRule)
            .validationStateIsUnknown(expiryDate = true)
            .enabledStateIs(submitButton = false)

        // Enter a partial cvc and check the validation state has not been updated
        CardFragmentTestUtils(activityRule)
            .enterCardDetails(cvc = "11")
            .validationStateIsUnknown(cvc = true)
            .enabledStateIs(submitButton = false)

        rotateLandscape(activityRule)

        // Check no change on rotation
        CardFragmentTestUtils(activityRule)
            .validationStateIsUnknown(cvc = true)
            .enabledStateIs(submitButton = false)

        // Enter an invalid luhn, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvc = unknownCvc, expiryDate = "1299")
            .validationStateIs(pan = false, cvc = false, expiryDate = true)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)

        rotatePortrait(activityRule)

        // Re-enter a luhn valid, mastercard identified card and valid date
        CardFragmentTestUtils(activityRule)
            .validationStateIs(pan = false, cvc = false, expiryDate = true)
            .hasBrand(MASTERCARD)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = luhnValidMastercardCard, cvc = "123", expiryDate = "1299")
            .cardDetailsAre(pan = luhnValidMastercardCard, cvc = "123", expiryDate = "12/99")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        rotateLandscape(activityRule)

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .enterCardDetails(pan = luhnValidMastercardCard, cvc = "12", expiryDate = "1299")
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)

        rotatePortrait(activityRule)

        CardFragmentTestUtils(activityRule)
            .hasBrand(MASTERCARD)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)
            .enterCardDetails(pan = luhnValidMastercardCard, cvc = "123", expiryDate = "1299")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

}
