package com.worldpay.access.checkout

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.Surface
import android.view.View
import android.view.accessibility.AccessibilityWindowInfo
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiDevice.getInstance
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.worldpay.access.checkout.AbstractUITest.CardBrand
import com.worldpay.access.checkout.matchers.AlphaMatcher
import com.worldpay.access.checkout.matchers.BrandVectorImageMatcher
import com.worldpay.access.checkout.matchers.BrandVectorImageNameMatcher
import com.worldpay.access.checkout.matchers.EditTextColorMatcher
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Assert.assertTrue
import java.util.concurrent.TimeUnit

object UITestUtils {

    val cardNumberMatcher: Matcher<View> = withId(R.id.card_number_edit_text)
    val cvvMatcher: Matcher<View> = withId(R.id.cardCVVText)
    val monthMatcher: Matcher<View> = withId(R.id.month_edit_text)
    val yearMatcher: Matcher<View> = withId(R.id.year_edit_text)
    val brandImageMatcher: Matcher<View> = withId(R.id.logo_view)
    private val cardExpiryMatcher: Matcher<View> = withId(R.id.cardExpiryText)
    private val buttonMatcher: Matcher<View> = withId(R.id.submit)
    private val progressMatcher: Matcher<View> = withId(R.id.loading_bar)

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

        checkSubmitInState(enabled = false)

        onView(progressMatcher)
            .check(matches(CoreMatchers.not(isDisplayed())))
    }

    fun typeFormInputs(card: String, cvv: String, month: String, year: String, assertInsertedCompleteText: Boolean = false) {
        typeCardDetails(card)
        typeCVVDetails(cvv)
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
            .perform(click(), ViewActions.replaceText(pan), ViewActions.closeSoftKeyboard())
    }

    fun updateCVVDetails(cvv: String) {
        onView(cvvMatcher)
            .perform(click(), ViewActions.replaceText(cvv), ViewActions.closeSoftKeyboard())
    }

    fun updateMonthDetails(month: String) {
        onView(monthMatcher)
            .perform(click(), ViewActions.replaceText(month), ViewActions.closeSoftKeyboard())
    }

    fun updateYearDetails(year: String) {
        onView(yearMatcher)
            .perform(click(), ViewActions.replaceText(year), ViewActions.closeSoftKeyboard())
    }

    fun checkSubmitInState(enabled: Boolean) {
        val enabledMatcher: Matcher<View> =
            if (enabled) isEnabled() else CoreMatchers.not(isEnabled())
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(enabledMatcher))
    }

    fun checkFieldInState(shouldBeValid: Boolean, viewMatcher: Matcher<View>, context: Context) {
        val expectedColor = if (shouldBeValid) getSuccessColor(context) else getFailColor(context)

        onView(viewMatcher)
            .check(matches(EditTextColorMatcher.withEditTextColor(expectedColor)))
    }

    fun checkFieldText(viewMatcher: Matcher<View>, expectedText: String) {
        onView(viewMatcher)
            .check(matches(withText(expectedText)))
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
        val resName = InstrumentationRegistry.getInstrumentation().targetContext.resources.getResourceName(resId)
        val selector = UiSelector().resourceId(resName)
        return getInstance(InstrumentationRegistry.getInstrumentation()).findObject(selector)
    }

    fun assertInProgressState() {
        assertTrue(progressBar().exists())
        closeKeyboard()
        assertFieldsAndSubmitButtonIsDisabled()
    }

    fun closeKeyboard() {
        if (isKeyboardOpened()) {
            getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        }
    }

    fun assertDisplaysResponseFromServer(responseString: String, view: View) {
        val expectedToastText: String = if (responseString.contains("Error")) {
            responseString
        } else {
            "Ref: $responseString"
        }
        onView(withText(expectedToastText))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(view)))
            .check(matches(isDisplayed()))
    }

    fun moveToField(viewMatcher: Matcher<View>) {
        onView(viewMatcher)
            .perform(click(), ViewActions.closeSoftKeyboard())
    }

    fun assertBrandImage(expectedImage: Int) {
        onView(brandImageMatcher)
            .check(matches(isDisplayed()))
            .check(matches(BrandVectorImageMatcher.withBrandVectorImageId(expectedImage)))
    }

    fun assertBrandImage(cardBrand: CardBrand) {
        onView(brandImageMatcher)
            .check(matches(isDisplayed()))
            .check(matches(BrandVectorImageNameMatcher.withBrandVectorImageName(cardBrand)))
    }

    fun getSuccessColor(context: Context) = getColor(context.resources, R.color.SUCCESS, context.theme)

    fun getFailColor(context: Context) = getColor(context.resources, R.color.FAIL, context.theme)

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

    fun assertUiObjectExistsAndIsDisabled(resId: Int) {
        val uiObject = uiObjectWithId(resId)
        uiObject.exists()
        Assert.assertFalse(uiObject.isEnabled)
    }

    fun assertUiObjectExistsAndIsEnabled(resId: Int) {
        val uiObject = uiObjectWithId(resId)
        uiObject.exists()
        assertTrue(uiObject.isEnabled)
    }

    fun rotateToPortraitAndWait(activity: Activity, timeoutInMillis: Long, assertionCondition: () -> Boolean) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        Awaitility.await().atMost(timeoutInMillis, TimeUnit.MILLISECONDS).until {
            when (UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).displayRotation) {
                Surface.ROTATION_0 -> true
                Surface.ROTATION_180 -> true
                else -> false
            } && assertionCondition()
        }
    }

    fun rotateToLandscapeAndWait(activity: Activity, timeoutInMillis: Long, assertionCondition: () -> Boolean) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        Awaitility.await().atMost(timeoutInMillis, TimeUnit.MILLISECONDS).until {
            when (UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).displayRotation) {
                Surface.ROTATION_90 -> true
                Surface.ROTATION_270 -> true
                else -> false
            } && assertionCondition()
        }
    }
}