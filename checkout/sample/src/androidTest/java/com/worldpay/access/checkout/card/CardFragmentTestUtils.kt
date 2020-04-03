package com.worldpay.access.checkout.card

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.uiautomator.UiObject
import com.worldpay.access.checkout.AbstractUITest
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.UITestUtils
import com.worldpay.access.checkout.UITestUtils.assertUiObjectExistsAndIsDisabled
import com.worldpay.access.checkout.UITestUtils.checkFieldText
import com.worldpay.access.checkout.matchers.AlphaMatcher
import com.worldpay.access.checkout.matchers.BrandVectorImageMatcher
import com.worldpay.access.checkout.matchers.BrandVectorImageNameMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Assert

object CardFragmentTestUtils {

    val cardNumberMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_number_edit_text)
    val cvvMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_flow_text_cvv)
    val monthMatcher: Matcher<View> = ViewMatchers.withId(R.id.month_edit_text)
    val yearMatcher: Matcher<View> = ViewMatchers.withId(R.id.year_edit_text)
    private val brandImageMatcher: Matcher<View> = ViewMatchers.withId(R.id.logo_view)
    private val cardExpiryMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_flow_text_exp)
    private val buttonMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_flow_btn_submit)
    private val progressMatcher: Matcher<View> = ViewMatchers.withId(R.id.loading_bar)

    fun assertInProgressState() {
        Assert.assertTrue(
            progressBar()
                .exists())
        UITestUtils.closeKeyboard()
        assertFieldsAndSubmitButtonIsDisabled()
    }

    fun assertValidInitialUIFields() {
        Espresso.onView(cardNumberMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(cvvMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(cardExpiryMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        checkSubmitInState(
            enabled = false
        )

        Espresso.onView(progressMatcher)
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
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
        Espresso.onView(cardNumberMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(pan), ViewActions.closeSoftKeyboard())
    }

    fun updateCVVDetails(cvv: String) {
        Espresso.onView(cvvMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(cvv), ViewActions.closeSoftKeyboard())
    }

    fun updateMonthDetails(month: String) {
        Espresso.onView(monthMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(month), ViewActions.closeSoftKeyboard())
    }

    fun updateYearDetails(year: String) {
        Espresso.onView(yearMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(year), ViewActions.closeSoftKeyboard())
    }

    fun checkSubmitInState(enabled: Boolean) {
        val enabledMatcher: Matcher<View> =
            if (enabled) ViewMatchers.isEnabled() else CoreMatchers.not(ViewMatchers.isEnabled())
        Espresso.onView(buttonMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(enabledMatcher))
    }

    fun assertFieldsAlpha(alpha: Float) {
        Espresso.onView(cardNumberMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
        Espresso.onView(cvvMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
        Espresso.onView(monthMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
        Espresso.onView(yearMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
        Espresso.onView(buttonMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
    }

    fun assertBrandImage(expectedImage: Int) {
        Espresso.onView(brandImageMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(
                ViewAssertions.matches(
                    BrandVectorImageMatcher.withBrandVectorImageId(
                        expectedImage
                    )
                )
            )
    }

    fun assertBrandImage(cardBrand: AbstractUITest.CardBrand) {
        Espresso.onView(brandImageMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(
                ViewAssertions.matches(
                    BrandVectorImageNameMatcher.withBrandVectorImageName(
                        cardBrand
                    )
                )
            )
    }

    private fun typeYear(year: String) {
        Espresso.onView(yearMatcher)
            .perform(ViewActions.typeText(year), ViewActions.closeSoftKeyboard())
    }

    private fun typeMonth(month: String) {
        Espresso.onView(monthMatcher)
            .perform(ViewActions.typeText(month), ViewActions.closeSoftKeyboard())
    }

    private fun typeCVVDetails(cvv: String) {
        Espresso.onView(cvvMatcher)
            .perform(ViewActions.typeText(cvv), ViewActions.closeSoftKeyboard())
    }

    private fun typeCardDetails(card: String) {
        Espresso.onView(cardNumberMatcher)
            .perform(ViewActions.typeText(card), ViewActions.closeSoftKeyboard())
    }

    private fun progressBar(): UiObject =
        UITestUtils.uiObjectWithId(
            R.id.loading_bar
        )

    private fun assertFieldsAndSubmitButtonIsDisabled() {
        assertUiObjectExistsAndIsDisabled(R.id.card_number_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.card_flow_text_cvv)
        assertUiObjectExistsAndIsDisabled(R.id.month_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.year_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.card_flow_btn_submit)
    }

}