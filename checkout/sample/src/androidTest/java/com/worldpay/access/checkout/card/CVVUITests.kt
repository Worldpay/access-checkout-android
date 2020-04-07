package com.worldpay.access.checkout.card

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.card.testutil.AbstractCardFlowUITest
import com.worldpay.access.checkout.card.testutil.CardBrand
import com.worldpay.access.checkout.card.testutil.CardBrand.AMEX
import com.worldpay.access.checkout.testutil.UITestUtils.getFailColor
import com.worldpay.access.checkout.testutil.UITestUtils.getSuccessColor
import com.worldpay.access.checkout.testutil.matchers.BrandVectorImageMatcher.Companion.withBrandVectorImageId
import com.worldpay.access.checkout.testutil.matchers.BrandVectorImageNameMatcher.Companion.withBrandVectorImageName
import com.worldpay.access.checkout.testutil.matchers.EditTextColorMatcher.Companion.withEditTextColor
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CVVUITests: AbstractCardFlowUITest() {

    private val amexCard = "343434343434343"
    private val masterCard = "5555555555554444"

    @Test
    fun cardCVVExists() {
        onView(withId(R.id.card_flow_text_cvv)).check(matches(isDisplayed()))
    }

    @Test
    fun givenUserAttemptsToTypeMoreThanMaxAllowedCVVLength_ThenFieldRestrictsToMaxLength() {
        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12345"))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withText("1234")))
    }

    @Test
    fun givenUserAttemptsToTypeLessThanMinAllowedCVVLength_ThenFieldDisplaysErrorWhenUserMovesAway() {
        closeSoftKeyboard()
        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12"))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))


        onView(withId(R.id.card_number_edit_text))
            .perform(click())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))
    }

    @Test
    fun givenNoCardDataEnteredThenCVVFieldShouldBeValidUpTo4Digits() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown_logo)))

        onView(withId(R.id.card_flow_text_cvv))
            .perform(pressImeActionButton())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), replaceText("123"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenCardDataEnteredThenRemovedAgainCVVFieldShouldBeValidUpTo4Digits() {
        closeSoftKeyboard()
        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("77"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withText("1234")))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenAmex_AndA4DigitCVV_shouldValidateCVV() {
        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(amexCard), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(AMEX)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenAmex_AndUserAttemptsToTypeMoreThanMaxAllowedCVVLength_ThenFieldRestrictsToMaxLengthForAmex() {
        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(amexCard), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(AMEX)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12345"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withText("1234")))
    }

    @Test
    fun givenAmex_AndA3DigitCVV_shouldInvalidateCVVOnceOffFocus() {
        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(amexCard), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(AMEX)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))
    }

    @Test
    fun givenAmex_AndA3DigitCVV_AndACorrectionToCardNumberToMastercard_shouldInvalidateAndRevalidateCVV() {
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(amexCard), pressImeActionButton())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(AMEX)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), pressImeActionButton())
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(masterCard), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withText("123")))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenVisa_AndA3DigitCVV_shouldValidateCVV() {
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("44"), pressImeActionButton())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.VISA)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenVisa_AndA4DigitCVV_shouldRestrictLengthTo3() {
        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("44"), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.VISA)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withText("123")))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        closeSoftKeyboard()
    }

    @Test
    fun givenCVVEnteredFirst_AndVisaIdentifiedAfter_ThenCVVFieldShouldBeReValidated() {
        closeSoftKeyboard()
        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("44"), pressImeActionButton())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.VISA)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), replaceText("123"), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

    }

    @Test
    fun givenMastercard_AndA3DigitCVV_shouldValidateCVV() {
        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55"), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.MASTERCARD)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenMastercard_AndA4DigitCVV_shouldRestrictLengthTo3() {
        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55"), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.MASTERCARD)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withText("123")))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

    }

    @Test
    fun givenCVVEnteredFirst_AndMastercardIdentifiedAfter_ThenCVVFieldShouldBeReValidated() {
        closeSoftKeyboard()
        onView(withId(R.id.card_flow_text_cvv))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55"), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.MASTERCARD)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), replaceText("123"), closeSoftKeyboard())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenUnidentifiedBrand_ThenCVVFieldShouldBeValidAt3And4Characters() {
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("77"), pressImeActionButton())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown_logo)))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("123"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("1234"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenUserLongClicksAndPastesTooLongStringIntoCvvFieldThenTheMaximumAcceptedLengthShouldBeApplied() {
        closeSoftKeyboard()
        val pastedText = "12345678"

        onView(withId(R.id.card_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(replaceText(pastedText))
            .check(matches(withText(pastedText.substring(0, 4))))
    }
}
