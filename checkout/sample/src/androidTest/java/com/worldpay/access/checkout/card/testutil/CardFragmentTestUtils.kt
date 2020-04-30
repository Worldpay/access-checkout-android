package com.worldpay.access.checkout.card.testutil

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.uiautomator.UiObject
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutil.UITestUtils.assertUiObjectExistsAndIsDisabled
import com.worldpay.access.checkout.testutil.UITestUtils.checkFieldText
import com.worldpay.access.checkout.testutil.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.testutil.matchers.AlphaMatcher
import com.worldpay.access.checkout.testutil.matchers.BrandVectorImageMatcher
import com.worldpay.access.checkout.testutil.matchers.BrandVectorImageNameMatcher
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.Assert.assertTrue

object CardFragmentTestUtils {

    val cardNumberMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_number_edit_text)
    val cvvMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_flow_text_cvv)
    val monthMatcher: Matcher<View> = ViewMatchers.withId(R.id.month_edit_text)
    val yearMatcher: Matcher<View> = ViewMatchers.withId(R.id.year_edit_text)
    private val brandImageMatcher: Matcher<View> = ViewMatchers.withId(R.id.logo_view)
    private val cardExpiryMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_flow_text_exp)
    private val submitButtonMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_flow_btn_submit)
    private val progressMatcher: Matcher<View> = ViewMatchers.withId(R.id.loading_bar)

    fun assertInProgressState() {
        assertTrue(progressBar().waitForExists(3000))
        closeKeyboard()
        assertFieldsAndSubmitButtonIsDisabled()
    }

    fun assertValidInitialUIFields() {
        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(cvvMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(cardExpiryMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        checkSubmitInState(
            enabled = false
        )

        onView(progressMatcher)
            .check(matches(not(isDisplayed())))
    }

    fun typeFormInputs(card: String, cvv: String, month: String, year: String, assertInsertedCompleteText: Boolean = false) {
        typeCardDetails(
            card
        )
        typeCVVDetails(
            cvv
        )
        typeMonth(month)
        typeYear(year)

        if (assertInsertedCompleteText) {
            checkFieldText(cardNumberMatcher, card)
            checkFieldText(cvvMatcher, cvv)
            checkFieldText(monthMatcher, month)
            checkFieldText(yearMatcher, year)
        }
    }

    fun updatePANDetails(pan: String) {
        onView(cardNumberMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(pan), ViewActions.closeSoftKeyboard())
    }

    fun updateCVVDetails(cvv: String) {
        onView(cvvMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(cvv), ViewActions.closeSoftKeyboard())
    }

    fun updateMonthDetails(month: String) {
        onView(monthMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(month), ViewActions.closeSoftKeyboard())
    }

    fun updateYearDetails(year: String) {
        onView(yearMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(year), ViewActions.closeSoftKeyboard())
    }

    fun checkSubmitInState(enabled: Boolean) {
        val enabledMatcher: Matcher<View> =
            if (enabled) isEnabled() else not(isEnabled())
        onView(submitButtonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(enabledMatcher))
    }

    fun assertFieldsAlpha(alpha: Float) {
        onView(cardNumberMatcher)
            .check(matches(AlphaMatcher.withAlpha(alpha)))
        onView(cvvMatcher)
            .check(matches(AlphaMatcher.withAlpha(alpha)))
        onView(monthMatcher)
            .check(matches(AlphaMatcher.withAlpha(alpha)))
        onView(yearMatcher)
            .check(matches(AlphaMatcher.withAlpha(alpha)))
        onView(submitButtonMatcher)
            .check(matches(AlphaMatcher.withAlpha(alpha)))
    }

    fun assertBrandImage(expectedImage: Int) {
        onView(brandImageMatcher)
            .check(matches(isDisplayed()))
            .check(
                matches(
                    BrandVectorImageMatcher.withBrandVectorImageId(
                        expectedImage
                    )
                )
            )
    }

    fun assertBrandImage(cardBrand: CardBrand) {
        onView(brandImageMatcher)
            .check(matches(isDisplayed()))
            .check(
                matches(
                    BrandVectorImageNameMatcher.withBrandVectorImageName(
                        cardBrand
                    )
                )
            )
    }

    private fun typeYear(year: String) {
        onView(yearMatcher)
            .perform(ViewActions.typeText(year), ViewActions.closeSoftKeyboard())
    }

    private fun typeMonth(month: String) {
        onView(monthMatcher)
            .perform(ViewActions.typeText(month), ViewActions.closeSoftKeyboard())
    }

    private fun typeCVVDetails(cvv: String) {
        onView(cvvMatcher)
            .perform(ViewActions.typeText(cvv), ViewActions.closeSoftKeyboard())
    }

    private fun typeCardDetails(card: String) {
        onView(cardNumberMatcher)
            .perform(ViewActions.typeText(card), ViewActions.closeSoftKeyboard())
    }

    private fun progressBar(): UiObject = uiObjectWithId(R.id.loading_bar)

    private fun assertFieldsAndSubmitButtonIsDisabled() {
        assertUiObjectExistsAndIsDisabled(R.id.card_number_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.card_flow_text_cvv)
        assertUiObjectExistsAndIsDisabled(R.id.month_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.year_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.card_flow_btn_submit)
    }

}