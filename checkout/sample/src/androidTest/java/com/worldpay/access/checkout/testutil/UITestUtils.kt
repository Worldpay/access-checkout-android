package com.worldpay.access.checkout.testutil

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityWindowInfo
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice.getInstance
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.worldpay.access.checkout.R
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import java.util.concurrent.TimeUnit

object UITestUtils {

    fun uiObjectWithId(resId: Int): UiObject {
        val resName = getInstrumentation().targetContext.resources.getResourceName(resId)
        val selector = UiSelector().resourceId(resName)
        return getInstance(getInstrumentation()).findObject(selector)
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

    fun getSuccessColor(context: Context) = getColor(context.resources,
        R.color.SUCCESS, context.theme)

    fun getFailColor(context: Context) = getColor(context.resources,
        R.color.FAIL, context.theme)

    private fun isKeyboardOpened(): Boolean {
        for (window in getInstrumentation().uiAutomation.windows) {
            if (window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                return true
            }
        }
        return false
    }

    fun closeKeyboard() {
        if (isKeyboardOpened()) {
            getInstance(getInstrumentation()).pressBack()
        }
    }

    fun setOrientationLeft() {
        val uiDevice = getInstance(getInstrumentation())
        uiDevice.setOrientationLeft()
    }

    fun setOrientationNatural() {
        val uiDevice = getInstance(getInstrumentation())
        uiDevice.setOrientationNatural()
    }

    fun reopenApp() {
        val uiDevice = getInstance(getInstrumentation())
        uiDevice.pressRecentApps()

        await().atMost(5, TimeUnit.SECONDS).until {
            Thread.sleep(500)
            uiDevice.pressRecentApps()

            onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()))

            true
        }
    }

    fun navigateTo(fragmentId: Int) {
        onView(withId(R.id.drawer_layout))
            .check(matches(isDisplayed()))
            .perform(open())
            .check(matches(isOpen()))
        onView(withId(R.id.nav_view))
            .check(matches(isDisplayed()))
            .perform(NavigationViewActions.navigateTo(fragmentId))
    }

}