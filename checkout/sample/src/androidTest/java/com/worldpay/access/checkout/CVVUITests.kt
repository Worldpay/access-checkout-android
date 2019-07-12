package com.worldpay.access.checkout


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.worldpay.access.checkout.UITestUtils.getFailColor
import com.worldpay.access.checkout.UITestUtils.getSuccessColor
import com.worldpay.access.checkout.matchers.BrandVectorImageMatcher.Companion.withBrandVectorImageId
import com.worldpay.access.checkout.matchers.BrandVectorImageNameMatcher.Companion.withBrandVectorImageName
import com.worldpay.access.checkout.matchers.EditTextColorMatcher.Companion.withEditTextColor
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CVVUITests: AbstractUITest() {

    val amexCard = "343434343434343"
    val masterCard = "5555555555554444"

    @Test
    fun cardCVVExists() {
        onView(withId(R.id.cardCVVText)).check(matches(isDisplayed()))
    }

    @Test
    fun givenUserAttemptsToTypeMoreThanMaxAllowedCVVLength_ThenFieldRestrictsToMaxLength() {
        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12345"))

        onView(withId(R.id.cardCVVText))
            .check(matches(withText("1234")))
    }

    @Test
    fun givenUserAttemptsToTypeLessThanMinAllowedCVVLength_ThenFieldDisplaysErrorWhenUserMovesAway() {
        closeSoftKeyboard()
        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12"))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))


        onView(withId(R.id.card_number_edit_text))
            .perform(click())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))
    }

    @Test
    fun givenNoCardDataEnteredThenCVVFieldShouldBeValidUpTo4Digits() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown_logo)))

        onView(withId(R.id.cardCVVText))
            .perform(pressImeActionButton())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), replaceText("123"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenCardDataEnteredThenRemovedAgainCVVFieldShouldBeValidUpTo4Digits() {
        closeSoftKeyboard()
        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("77"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
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
            .check(matches(withBrandVectorImageName(CardBrand.AMEX)))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
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
            .check(matches(withBrandVectorImageName(CardBrand.AMEX)))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12345"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
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
            .check(matches(withBrandVectorImageName(CardBrand.AMEX)))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
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
            .check(matches(withBrandVectorImageName(CardBrand.AMEX)))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), pressImeActionButton())
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(masterCard), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
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

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
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

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
            .check(matches(withText("123")))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        closeSoftKeyboard()
    }

    @Test
    fun givenCVVEnteredFirst_AndVisaIdentifiedAfter_ThenCVVFieldShouldBeReValidated() {
        closeSoftKeyboard()
        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("44"), pressImeActionButton())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.VISA)))

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), replaceText("123"), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
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

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
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

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
            .check(matches(withText("123")))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

    }

    @Test
    fun givenCVVEnteredFirst_AndMastercardIdentifiedAfter_ThenCVVFieldShouldBeReValidated() {
        closeSoftKeyboard()
        onView(withId(R.id.cardCVVText))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1234"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55"), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageName(CardBrand.MASTERCARD)))

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), replaceText("123"), closeSoftKeyboard())

        onView(withId(R.id.cardCVVText))
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

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("12"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getFailColor(activityRule.activity))))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("123"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("1234"), pressImeActionButton())

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), pressImeActionButton())

        onView(withId(R.id.cardCVVText))
            .check(matches(withEditTextColor(getSuccessColor(activityRule.activity))))
    }

    @Test
    fun givenUserLongClicksAndPastesTooLongStringIntoCvvFieldThenTheMaximumAcceptedLengthShouldBeApplied() {
        closeSoftKeyboard()
        val pastedText = "12345678"

        onView(withId(R.id.cardCVVText))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(replaceText(pastedText))
            .check(matches(withText(pastedText.substring(0, 4))))
    }
}
