package com.worldpay.access.checkout

import android.content.Context
import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject
import android.support.test.uiautomator.UiSelector
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.content.res.ResourcesCompat.getColor
import android.view.View
import android.view.accessibility.AccessibilityWindowInfo
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Assert

object UITestUtils {

    val cardNumberMatcher: Matcher<View> = ViewMatchers.withId(R.id.card_number_edit_text)
    val cvvMatcher: Matcher<View> = ViewMatchers.withId(R.id.cardCVVText)
    val monthMatcher: Matcher<View> = ViewMatchers.withId(R.id.month_edit_text)
    val yearMatcher: Matcher<View> = ViewMatchers.withId(R.id.year_edit_text)
    private val cardExpiryMatcher: Matcher<View> = ViewMatchers.withId(R.id.cardExpiryText)
    private val buttonMatcher: Matcher<View> = ViewMatchers.withId(R.id.submit)
    private val progressMatcher: Matcher<View> = ViewMatchers.withId(R.id.loading_bar)

    fun assertValidInitialUIFields() {
        onView(cardNumberMatcher)
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isEnabled()))

        onView(cvvMatcher)
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isEnabled()))

        onView(cardExpiryMatcher)
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isEnabled()))

        checkSubmitInState(enabled = false)

        onView(progressMatcher)
            .check(matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    fun typeFormInputs(card: String, cvv: String, month: String, year: String) {
        typeCardDetails(card)
        typeCVVDetails(cvv)
        typeMonth(month)
        typeYear(year)
    }

    fun updatePANDetails(pan: String) {
        onView(cardNumberMatcher)
            .perform(ViewActions.replaceText(pan), ViewActions.closeSoftKeyboard())
    }

    fun updateCVVDetails(cvv: String) {
        onView(cvvMatcher)
            .perform(ViewActions.replaceText(cvv), ViewActions.closeSoftKeyboard())
    }

    fun updateMonthDetails(month: String) {
        onView(monthMatcher)
            .perform(ViewActions.replaceText(month), ViewActions.closeSoftKeyboard())
    }

    fun checkSubmitInState(enabled: Boolean) {
        val enabledMatcher: Matcher<View> =
            if (enabled) ViewMatchers.isEnabled() else CoreMatchers.not(ViewMatchers.isEnabled())
        onView(buttonMatcher)
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(enabledMatcher))
    }

    fun checkFieldIsValidState(shouldBeValid: Boolean, viewMatcher: Matcher<View>, context: Context) {
        val expectedColor = if (shouldBeValid) getSuccessColor(context) else getFailColor(context)

        onView(viewMatcher)
            .check(matches(EditTextColorMatcher.withEditTextColor(expectedColor)))
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
        onView(buttonMatcher)
            .check(matches(AlphaMatcher.withAlpha(alpha)))
    }

    fun uiObjectWithId(resId: Int): UiObject {
        val resName = InstrumentationRegistry.getTargetContext().resources.getResourceName(resId)
        val selector = UiSelector().resourceId(resName)
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).findObject(selector)
    }

    fun assertInProgressState() {
        Assert.assertTrue(progressBar().exists())
        if (isKeyboardOpened())
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        assertFieldsAndSubmitButtonIsDisabled()
    }

    fun assertDisplaysResponseFromServer(responseString: String, view: View) {
        val expectedToastText: String = if (responseString.contains("Error")) {
            responseString
        } else {
            "Ref: $responseString"
        }
        onView(ViewMatchers.withText(expectedToastText))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(view)))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    private fun getSuccessColor(context: Context) = getColor(context.resources, R.color.SUCCESS, context.theme)

    private fun getFailColor(context: Context) = getColor(context.resources, R.color.FAIL, context.theme)

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

    private fun assertFieldsAndSubmitButtonIsDisabled() {
        assertUiObjectExistsAndIsDisabled(R.id.card_number_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.cardCVVText)
        assertUiObjectExistsAndIsDisabled(R.id.month_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.year_edit_text)
        assertUiObjectExistsAndIsDisabled(R.id.submit)
    }


    private fun isKeyboardOpened(): Boolean {
        for (window in InstrumentationRegistry.getInstrumentation().uiAutomation.windows) {
            if (window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                return true
            }
        }
        return false
    }

    private fun progressBar(): UiObject =
        uiObjectWithId(R.id.loading_bar)

    private fun assertUiObjectExistsAndIsDisabled(resId: Int) {
        val uiObject = uiObjectWithId(resId)
        uiObject.exists()
        Assert.assertFalse(uiObject.isEnabled)
    }
}