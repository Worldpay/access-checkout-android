package com.worldpay.access.checkout.sample.card

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardBrand.*
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.CVC
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.EXPIRY_DATE
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PANUITest: AbstractCardFragmentTest() {

    @Test
    fun givenUserClicksCardViewAndInsertsUnknownPartialCardNumberThenTextShouldOnlyShowInvalidWhenFocusIsLostDisplayUnknownCardIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1111111")
            .hasNoBrand()
            .focusOn(CVC)
            .validationStateIs(pan = false)
            .hasNoBrand()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsValidVisaCardNumberThenTextShouldTurnGreenAndDisplayVisaIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "4026344341791618")
            .validationStateIs(pan = true)
            .hasBrand(VISA)
    }

    @Test
    fun shouldDisplayJCBIconWhenJCBPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1800")
            .hasBrand(JCB)
    }

    @Test
    fun shouldDisplayDiscoverIconWhenDiscoverPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "6011")
            .hasBrand(DISCOVER)
    }

    @Test
    fun shouldDisplayMaestroIconWhenMaestroPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "493698")
            .hasBrand(MAESTRO)
    }

    @Test
    fun shouldDisplayDinersIconWhenDinersPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "305")
            .hasBrand(DINERS)
    }

    

    @Test
    fun givenUserLongClicksAndPastesTooLongStringIntoPanFieldThenTheMaximumAcceptedLengthShouldBeApplied() {
        val pastedText = "123456789012345678901234567890"

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = pastedText)
            .cardDetailsAre(pan = pastedText.substring(0, 19))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidVisaCardNumberThenTextShouldTurnRedWhenFocusIsLostAndDisplayVisaIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "4024001728904375123")
            .hasBrand(VISA)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidMastercardCardNumberThenTextShouldTurnGreenAndDisplayMastercardIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "5555555555554444")
            .validationStateIs(pan = true)
            .hasBrand(MASTERCARD)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidMastercardCardNumberThenTextShouldTurnRedWhenFocusIsLostAndDisplayMastercardIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "5555555555554443")
            .hasBrand(MASTERCARD)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidAmexCardNumberThenTextShouldTurnGreenAndDisplayAmexIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "343434343434343")
            .validationStateIs(pan = true)
            .hasBrand(AMEX)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidAmexCardNumberThenTextShouldOnlyBeInvalidWhenFocusIsLostAndDisplayAmexIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "343434343434341")
            .hasBrand(AMEX)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidUnknownCardNumberThenTextShouldTurnGreenAndDisplayUnknownIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1111111111111117")
            .validationStateIs(pan = true)
            .hasNoBrand()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidUnknownCardNumberThenTextShouldTurnRedWhenFocusIsLostAndDisplayUnknownIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1111111111111111112")
            .hasNoBrand()
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenShouldDisplayVisaIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "44")
            .hasBrand(VISA)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialMastercardCardNumberThenTextShouldDisplayMastercardIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "22")
            .hasBrand(MASTERCARD)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialAmexCardNumberThenTextShouldDisplayAmexIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "34")
            .hasBrand(AMEX)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenChangesToPartialMastercardTextIconShouldBeMastercardAndShouldNotBeInvalid() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "44000000")
            .hasBrand(VISA)
            .enterCardDetails(pan = "")
            .hasNoBrand()
            .enterCardDetails(pan = "55000000")
            .hasBrand(MASTERCARD)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenTextShouldDisplayVisaIconButDisplayErrorTextWhenFocusIsLost() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "40")
            .hasBrand(VISA)
            .focusOn(EXPIRY_DATE)
            .validationStateIs(pan = false)
            .hasBrand(VISA)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsVisaIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        val validVisaCardNumber = "4929867126833626469"

        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = validVisaCardNumber)
            .validationStateIs(pan = true)
            .hasBrand(VISA)

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("1"), pressImeActionButton())
            .check(matches(withText(validVisaCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsMastercardIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        val validMastercardCardNumber = "5555555555554444"

        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = validMastercardCardNumber)
            .validationStateIs(pan = true)
            .hasBrand(MASTERCARD)

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("4"), pressImeActionButton())
            .check(matches(withText(validMastercardCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsAmexIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        val validAmexCardNumber = "343434343434343"

        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = validAmexCardNumber)
            .validationStateIs(pan = true)
            .hasBrand(AMEX)

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("4"), closeSoftKeyboard())
            .check(matches(withText(validAmexCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsUnidentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        val validUnidentifiedCardNumber = "0000000000000000000"

        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = validUnidentifiedCardNumber)
            .validationStateIs(pan = true)
            .hasNoBrand()

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("0"), closeSoftKeyboard())
            .check(matches(withText(validUnidentifiedCardNumber)))
    }

}


