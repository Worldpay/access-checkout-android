package com.worldpay.access.checkout.sample.testutil

import android.view.accessibility.AccessibilityWindowInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice.getInstance
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.worldpay.access.checkout.sample.R
import org.awaitility.Awaitility.await
import java.util.concurrent.TimeUnit

object UITestUtils {

    fun uiObjectWithId(resId: Int): UiObject {
        val resName = getInstrumentation().targetContext.resources.getResourceName(resId)
        val selector = UiSelector().resourceId(resName)
        return getInstance(getInstrumentation()).findObject(selector)
    }

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
        if (uiDevice.isNaturalOrientation) {
            uiDevice.setOrientationLeft()
        }
    }

    fun setOrientationNatural() {
        val uiDevice = getInstance(getInstrumentation())
        if (!uiDevice.isNaturalOrientation) {
            uiDevice.setOrientationNatural()
        }
    }

    fun reopenApp() {
        val uiDevice = getInstance(getInstrumentation())
        uiDevice.pressRecentApps()

        await().atMost(5, TimeUnit.SECONDS).until {
            Thread.sleep(500)
            uiDevice.pressRecentApps()

            try {
                onView(withId(R.id.drawer_layout))
                    .check(matches(isDisplayed()))
            } catch (e: NoMatchingViewException) {
                onView(withId(android.R.id.button1))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
            }

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
