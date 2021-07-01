package com.worldpay.access.checkout.sample.card.standard

import android.view.KeyEvent.KEYCODE_DEL
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.AMEX_PAN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.PARTIAL_AMEX
import com.worldpay.access.checkout.sample.card.CardNumberUtil.PARTIAL_MASTERCARD
import com.worldpay.access.checkout.sample.card.CardNumberUtil.PARTIAL_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.AMEX
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.DINERS
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.DISCOVER
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.JCB
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.MAESTRO
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.CVC
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.EXPIRY_DATE
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PANUITest : AbstractCardFragmentTest() {

    @Test
    fun givenUserClicksCardViewAndInsertsUnknownPartialCardNumberThenTextShouldOnlyShowInvalidWhenFocusIsLostDisplayUnknownCardIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_UNKNOWN_LUHN)
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
            .enterCardDetails(pan = VISA_PAN)
            .validationStateIs(pan = true)
            .hasBrand(VISA)
    }

    @Test
    fun shouldDisplayJCBIconWhenJCBPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = JCB_PAN)
            .hasBrand(JCB)
    }

    @Test
    fun shouldDisplayDiscoverIconWhenDiscoverPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = DISCOVER_PAN)
            .hasBrand(DISCOVER)
    }

    @Test
    fun shouldDisplayMaestroIconWhenMaestroPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = MAESTRO_PAN)
            .hasBrand(MAESTRO)
    }

    @Test
    fun shouldDisplayDinersIconWhenDinersPanIsEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = DINERS_PAN)
            .hasBrand(DINERS)
    }

    @Test
    fun givenUserLongClicksAndPastesTooLongStringIntoPanFieldThenTheMaximumAcceptedLengthShouldBeApplied() {
        val pastedText = "123456789012345678901234567890"
        val pastedTextWith19DigitsAndSpaces = "1234 5678 9012 3456 789"

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = pastedText)
            .cardDetailsAre(pan = pastedTextWith19DigitsAndSpaces)
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
            .enterCardDetails(pan = MASTERCARD_PAN)
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
            .enterCardDetails(pan = AMEX_PAN)
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
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN)
            .validationStateIs(pan = true)
            .hasNoBrand()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidUnknownCardNumberThenTextShouldTurnRedWhenFocusIsLostAndDisplayUnknownIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = INVALID_UNKNOWN_LUHN)
            .hasNoBrand()
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenShouldDisplayVisaIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_VISA)
            .hasBrand(VISA)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialMastercardCardNumberThenTextShouldDisplayMastercardIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_MASTERCARD)
            .hasBrand(MASTERCARD)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialAmexCardNumberThenTextShouldDisplayAmexIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_AMEX)
            .hasBrand(AMEX)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenChangesToPartialMastercardTextIconShouldBeMastercardAndShouldNotBeInvalid() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_VISA)
            .hasBrand(VISA)
            .enterCardDetails(pan = "")
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_MASTERCARD)
            .hasBrand(MASTERCARD)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenTextShouldDisplayVisaIconButDisplayErrorTextWhenFocusIsLost() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_VISA)
            .hasBrand(VISA)
            .focusOn(EXPIRY_DATE)
            .validationStateIs(pan = false)
            .hasBrand(VISA)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsVisaIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        val validVisaCardNumber = "4929867126833626469"
        val validVisaCardNumberWithSpaces = "4929 8671 2683 3626 469"

        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = validVisaCardNumber)
            .validationStateIs(pan = true)
            .hasBrand(VISA)

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("1"), pressImeActionButton())
            .check(matches(withText(validVisaCardNumberWithSpaces)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsMastercardIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = MASTERCARD_PAN)
            .validationStateIs(pan = true)
            .hasBrand(MASTERCARD)

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("4"), pressImeActionButton())
            .check(matches(withText(MASTERCARD_PAN_FORMATTED)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsAmexIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = AMEX_PAN)
            .validationStateIs(pan = true)
            .hasBrand(AMEX)

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("4"), closeSoftKeyboard())
            .check(matches(withText(AMEX_PAN_FORMATTED)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsUnidentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN)
            .validationStateIs(pan = true)
            .hasNoBrand()

        onView(withId(R.id.card_flow_text_pan))
            .perform(typeTextIntoFocusedView("0"), closeSoftKeyboard())
            .check(matches(withText(VALID_UNKNOWN_LUHN_FORMATTED)))
    }

    @Test
    fun givenPanFormattingEnabledShouldFormatRecognisedNonAmexPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "41111")
            .hasBrand(VISA)
            .cardDetailsAre(pan = "4111 1")
            .enterCardDetails(pan = VISA_PAN)
            .validationStateIs(pan = true)
            .cardDetailsAre(pan = "4111 1111 1111 1111")
    }

    @Test
    fun givenPanFormattingEnabledShouldFormatUnrecognisedPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "11111")
            .hasNoBrand()
            .cardDetailsAre(pan = "1111 1")
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN)
            .validationStateIs(pan = true)
            .hasNoBrand()
            .cardDetailsAre(pan = VALID_UNKNOWN_LUHN_FORMATTED)
    }

    @Test
    fun givenPanFormattingEnabledShouldFormatAmexPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "37178")
            .hasBrand(AMEX)
            .cardDetailsAre(pan = "3717 8")
            .enterCardDetails(pan = AMEX_PAN)
            .validationStateIs(pan = true)
            .cardDetailsAre(pan = AMEX_PAN_FORMATTED)
    }

    @Test
    fun givenPanFormattingEnabledShouldFormatAmexPanAndPlaceCursorWhenTyping() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "37177")
            .setCursorPositionOnPan(3)

        onView(withId(R.id.card_flow_text_pan))
            .perform(
                typeTextIntoFocusedView("2"),
                typeTextIntoFocusedView("3"),
                typeTextIntoFocusedView("4567"),
                typeTextIntoFocusedView("8")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "3712 345678 77")
            .cursorPositionIs(11)
            .setCursorPositionOnPan(8)

        onView(withId(R.id.card_flow_text_pan))
            .perform(
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressImeActionButton()
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "3716 7877")
            .cursorPositionIs(3)
    }

    @Test
    fun givenPanFormattingEnabledShouldFormatNonAmexPanAndPlaceCursorWhenTyping() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1234")
            .setCursorPositionOnPan(3)

        onView(withId(R.id.card_flow_text_pan))
            .perform(
                typeTextIntoFocusedView("1"),
                typeTextIntoFocusedView("2"),
                typeTextIntoFocusedView("34567"),
                typeTextIntoFocusedView("8")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1231 2345 6784")
            .cursorPositionIs(13)
            .setCursorPositionOnPan(8)

        onView(withId(R.id.card_flow_text_pan))
            .perform(
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressImeActionButton()
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1235 6784")
            .cursorPositionIs(3)
    }

    @Test
    fun givenPanFormattingEnabledShouldDeleteDigitBeforeSpaceWhenDeletingSpace() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "12345")
            .cardDetailsAre(pan = "1234 5")
            .setCursorPositionOnPan(5)

        onView(withId(R.id.card_flow_text_pan))
            .perform(
                pressKey(KEYCODE_DEL),
                pressImeActionButton()
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1235")
            .cursorPositionIs(4)
    }
}
