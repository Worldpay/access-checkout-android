package com.worldpay.access.checkout

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject
import android.support.test.uiautomator.UiSelector
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
        Espresso.onView(cardNumberMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(cvvMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(cardExpiryMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        checkSubmitInState(enabled = false)

        Espresso.onView(progressMatcher)
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    fun typeFormInputs(card: String, cvv: String, month: String, year: String) {
        typeCardDetails(card)
        typeCVVDetails(cvv)
        typeMonth(month)
        typeYear(year)
    }

    fun updateCVVDetails(cvv: String) {
        Espresso.onView(cvvMatcher)
            .perform(ViewActions.replaceText(cvv), ViewActions.closeSoftKeyboard())
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
        val expectedToastText: String
        if (responseString.contains("Error"))
            expectedToastText = responseString
        else
            expectedToastText = "Ref: $responseString"
        Espresso.onView(ViewMatchers.withText(expectedToastText))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(view)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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