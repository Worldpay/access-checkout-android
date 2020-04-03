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
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiDevice.getInstance
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.worldpay.access.checkout.matchers.EditTextColorMatcher
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Assert.assertTrue
import java.util.concurrent.TimeUnit

object UITestUtils {

    fun uiObjectWithId(resId: Int): UiObject {
        val resName = InstrumentationRegistry.getInstrumentation().targetContext.resources.getResourceName(resId)
        val selector = UiSelector().resourceId(resName)
        return getInstance(InstrumentationRegistry.getInstrumentation()).findObject(selector)
    }

    fun checkFieldInState(shouldBeValid: Boolean, viewMatcher: Matcher<View>, context: Context) {
        val expectedColor = if (shouldBeValid) getSuccessColor(context) else getFailColor(
            context
        )

        onView(viewMatcher)
            .check(matches(EditTextColorMatcher.withEditTextColor(expectedColor)))
    }

    fun checkFieldText(viewMatcher: Matcher<View>, expectedText: String) {
        onView(viewMatcher)
            .check(matches(withText(expectedText)))
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

    fun getSuccessColor(context: Context) = getColor(context.resources, R.color.SUCCESS, context.theme)

    fun getFailColor(context: Context) = getColor(context.resources, R.color.FAIL, context.theme)

    private fun isKeyboardOpened(): Boolean {
        for (window in InstrumentationRegistry.getInstrumentation().uiAutomation.windows) {
            if (window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                return true
            }
        }
        return false
    }

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

    fun closeKeyboard() {
        if (isKeyboardOpened()) {
            getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
        }
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