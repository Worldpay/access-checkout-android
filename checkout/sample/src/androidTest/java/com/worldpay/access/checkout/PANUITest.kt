package com.worldpay.access.checkout

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.worldpay.access.checkout.UITestUtils.assertBrandImage
import com.worldpay.access.checkout.UITestUtils.cardNumberMatcher
import com.worldpay.access.checkout.UITestUtils.checkFieldInState
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class PANUITest: AbstractUITest() {


    @Test
    fun cardNumber_exists() {
        onView(withId(R.id.panView)).check(matches(isDisplayed()))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsUnknownPartiallyValidCardNumberThenTextShouldTurnGreenAndDisplayUnknownCardIcon() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1111111"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_unknown_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsValidVisaCardNumberThenTextShouldTurnGreenAndDisplayVisaIcon() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)


        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("4026344341791618"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)
    }

    @Test
    fun givenUserLongClicksAndPastesTooLongStringIntoPanFieldThenTheMaximumAcceptedLengthShouldBeApplied() {

        closeSoftKeyboard()
        val pastedText = "123456789012345678901234567890"

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(replaceText(pastedText))
            .check(matches(withText(pastedText.substring(0, 19))))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidVisaCardNumberThenTextShouldTurnRedAndDisplayVisaIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("4024001728904375"), pressImeActionButton())

        checkFieldInState(false, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidMastercardCardNumberThenTextShouldTurnGreenAndDisplayMastercardIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), pressImeActionButton())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_mastercard_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidMastercardCardNumberThenTextShouldTurnRedAndDisplayMastercardIcon() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554443"), closeSoftKeyboard())

        checkFieldInState(false, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_mastercard_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidAmexCardNumberThenTextShouldTurnGreenAndDisplayAmexIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText("343434343434343"), pressImeActionButton())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_amex_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidAmexCardNumberThenTextShouldTurnRedAndDisplayAmexIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("343434343434341"), pressImeActionButton())

        checkFieldInState(false, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_amex_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidUnknownCardNumberThenTextShouldTurnGreenAndDisplayUnknownIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1111111111111117"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_unknown_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidUnknownCardNumberThenTextShouldTurnRedAndDisplayUnknownIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

            .perform(click(), typeText("1111111111111111112"), closeSoftKeyboard())

        checkFieldInState(false, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_unknown_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenTextShouldDisplayVisaIcon() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("44"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)

    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialMastercardCardNumberThenTextShouldDisplayMastercardIcon() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText("22"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_mastercard_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialAmexCardNumberThenTextShouldDisplayAmexIcon() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("34"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_amex_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenChangesToPartialMastercardTextShouldNotIndicateInvalidAndIconShouldBeMastercard() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("44000000"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), closeSoftKeyboard())

        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55000000"), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_mastercard_logo)
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberAndMovesToDifferentFieldThenTextShouldDisplayVisaIconButDisplayErrorText() {
        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText("40"))

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)

        onView(withId(R.id.month_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(pressImeActionButton())

        checkFieldInState(false, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)
    }


    @Test
    fun givenUserClicksCardViewAndInsertsVisaIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        val validVisaCardNumber = "4026344341791618"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validVisaCardNumber), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), typeTextIntoFocusedView("1"), closeSoftKeyboard())
            .check(matches(withText(validVisaCardNumber)))
        closeSoftKeyboard()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsVisaIdentifiedCardNumberWhichMatchesSubRuleThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        // 413600 is a sub rule in the card config file with valid length of 13
        val validVisaCardNumber = "4136000000008"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validVisaCardNumber + "1"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .check(matches(withText(validVisaCardNumber)))

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_visa_logo)

        closeSoftKeyboard()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsMastercardIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        val validMastercardCardNumber = "5555555555554444"
        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validMastercardCardNumber), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_mastercard_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(typeTextIntoFocusedView("4"), pressImeActionButton())
            .check(matches(withText(validMastercardCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsAmexIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        val validAmexCardNumber = "343434343434343"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText(validAmexCardNumber), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_amex_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(typeTextIntoFocusedView("4"), closeSoftKeyboard())
            .check(matches(withText(validAmexCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsUnidentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        assertBrandImage(R.drawable.card_unknown_logo)

        val validUnidentifiedCardNumber = "0000000000000000000"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validUnidentifiedCardNumber), closeSoftKeyboard())

        checkFieldInState(true, cardNumberMatcher, activityRule.activity)

        assertBrandImage(R.drawable.card_unknown_logo)

        onView(withId(R.id.card_number_edit_text))
            .perform(typeTextIntoFocusedView("0"), closeSoftKeyboard())
            .check(matches(withText(validUnidentifiedCardNumber)))
    }

    @Test
    fun cardExpiry_exists() {
        onView(withId(R.id.cardExpiryText)).check(matches(isDisplayed()))
    }
}


