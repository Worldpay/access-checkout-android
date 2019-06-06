package com.worldpay.access.checkout

import android.graphics.drawable.BitmapDrawable
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.ImageView
import com.worldpay.access.checkout.BrandVectorImageMatcher.Companion.withBrandVectorImageId
import com.worldpay.access.checkout.UITestUtils.cardNumberMatcher
import com.worldpay.access.checkout.UITestUtils.checkFieldIsValidState
import com.worldpay.access.checkout.views.PANLayout
import org.hamcrest.Description
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class CardUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun cardNumber_exists() {
        onView(withId(R.id.panView)).check(matches(isDisplayed()))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsUnknownPartiallyValidCardNumberThenTextShouldTurnGreenAndDisplayUnknownCardIcon() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1111111"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsValidVisaCardNumberThenTextShouldTurnGreenAndDisplayVisaIcon() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("4026344341791618"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidVisaCardNumberThenTextShouldTurnRedAndDisplayVisaIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("4024001728904375"), pressImeActionButton())

        checkFieldIsValidState(false, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidMastercardCardNumberThenTextShouldTurnGreenAndDisplayMastercardIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), pressImeActionButton())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_mastercard)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidMastercardCardNumberThenTextShouldTurnRedAndDisplayMastercardIcon() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554443"), closeSoftKeyboard())

        checkFieldIsValidState(false, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_mastercard)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidAmexCardNumberThenTextShouldTurnGreenAndDisplayAmexIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        closeSoftKeyboard()
        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText("343434343434343"), pressImeActionButton())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_amex)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidAmexCardNumberThenTextShouldTurnRedAndDisplayAmexIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("343434343434341"), pressImeActionButton())

        checkFieldIsValidState(false, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_amex)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnValidUnknownCardNumberThenTextShouldTurnGreenAndDisplayUnknownIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1111111111111117"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsLuhnInvalidUnknownCardNumberThenTextShouldTurnRedAndDisplayUnknownIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

            .perform(click(), typeText("1111111111111111112"), closeSoftKeyboard())

        checkFieldIsValidState(false, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenTextShouldDisplayVisaIcon() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("44"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialMastercardCardNumberThenTextShouldDisplayMastercardIcon() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText("22"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_mastercard)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialAmexCardNumberThenTextShouldDisplayAmexIcon() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("34"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_amex)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberThenChangesToPartialMastercardTextShouldNotIndicateInvalidAndIconShouldBeMastercard() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("44000000"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55000000"), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_mastercard)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsPartialVisaCardNumberAndMovesToDifferentFieldThenTextShouldDisplayVisaIconButDisplayErrorText() {
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText("40"))

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))

        onView(withId(R.id.month_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(pressImeActionButton())

        checkFieldIsValidState(false, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))
    }


    @Test
    fun givenUserClicksCardViewAndInsertsVisaIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        val validVisaCardNumber = "4026344341791618"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validVisaCardNumber), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))

        onView(withId(R.id.card_number_edit_text))
            .perform(click(), typeTextIntoFocusedView("1"), closeSoftKeyboard())
            .check(matches(withText(validVisaCardNumber)))
        closeSoftKeyboard()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsVisaIdentifiedCardNumberWhichMatchesSubRuleThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        // 413600 is a sub rule in the card config file with valid length of 13
        val validVisaCardNumber = "4136000000008"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validVisaCardNumber + "1"), closeSoftKeyboard())

        onView(withId(R.id.card_number_edit_text))
            .check(matches(withText(validVisaCardNumber)))

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_visa)))

        closeSoftKeyboard()
    }

    @Test
    fun givenUserClicksCardViewAndInsertsMastercardIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        val validMastercardCardNumber = "5555555555554444"
        onView(withId(R.id.card_number_edit_text))
            .perform(closeSoftKeyboard())
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validMastercardCardNumber), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_mastercard)))

        onView(withId(R.id.card_number_edit_text))
            .perform(typeTextIntoFocusedView("4"), pressImeActionButton())
            .check(matches(withText(validMastercardCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsAmexIdentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        val validAmexCardNumber = "343434343434343"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(closeSoftKeyboard())
            .perform(click(), typeText(validAmexCardNumber), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_amex)))

        onView(withId(R.id.card_number_edit_text))
            .perform(typeTextIntoFocusedView("4"), closeSoftKeyboard())
            .check(matches(withText(validAmexCardNumber)))
    }

    @Test
    fun givenUserClicksCardViewAndInsertsUnidentifiedCardNumberThenTextFieldShouldRestrictBasedOnMaxLength() {
        closeSoftKeyboard()
        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        val validUnidentifiedCardNumber = "0000000000000000000"
        onView(withId(R.id.card_number_edit_text))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText(validUnidentifiedCardNumber), closeSoftKeyboard())

        checkFieldIsValidState(true, cardNumberMatcher, activityRule.activity)

        onView(withId(R.id.logo_view))
            .check(matches(isDisplayed()))
            .check(matches(withBrandVectorImageId(R.drawable.card_unknown)))

        onView(withId(R.id.card_number_edit_text))
            .perform(typeTextIntoFocusedView("0"), closeSoftKeyboard())
            .check(matches(withText(validUnidentifiedCardNumber)))
    }

    @Test
    fun cardExpiry_exists() {
        onView(withId(R.id.cardExpiryText)).check(matches(isDisplayed()))
    }
}

internal class BrandImageMatcher private constructor(private val id: Int) :
    BoundedMatcher<View, ImageView>(ImageView::class.java) {
    override fun matchesSafely(item: ImageView): Boolean {
        val context = item.context
        val expectedBitmap = context.getDrawable(id) as BitmapDrawable
        return (item.drawable as BitmapDrawable).bitmap.sameAs(expectedBitmap.bitmap)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable ID:")
            .appendValue(id)
    }

    companion object {
        fun withBrandImageId(id: Int): BrandImageMatcher {
            return BrandImageMatcher(id)
        }
    }
}

internal class BrandVectorImageMatcher private constructor(private val id: Int) :
    BoundedMatcher<View, ImageView>(ImageView::class.java) {
    override fun matchesSafely(item: ImageView): Boolean {
        val context = item.context
        val expectedResName = context.resources.getResourceEntryName(id)//getDrawable(id) as BitmapDrawable
        return (item.getTag(PANLayout.CARD_TAG)).equals(expectedResName)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable ID:")
            .appendValue(id)
    }

    companion object {

        fun withBrandVectorImageId(id: Int): BrandVectorImageMatcher {
            return BrandVectorImageMatcher(id)
        }
    }
}