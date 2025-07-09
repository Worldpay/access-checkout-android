package com.worldpay.access.checkout.sample.testutil

import android.content.pm.ActivityInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice.getInstance
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
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

    fun rotateLandscape(activityRule: ActivityTestRule<MainActivity>) {
        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        await().atMost(10, TimeUnit.SECONDS).until {

            val drawerIsVisible =
                activityRule.activity.findViewById<DrawerLayout>(R.id.drawer_layout).isVisible
            val progressBarIsVisible =
                activityRule.activity.findViewById<ProgressBar>(R.id.loading_bar).isVisible

            if (!drawerIsVisible && !progressBarIsVisible) {
                onView(withId(android.R.id.button1))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
            }

            Thread.sleep(2000)

            true
        }
    }

    fun rotatePortrait(activityRule: ActivityTestRule<MainActivity>) {
        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        await().atMost(10, TimeUnit.SECONDS).until {

            val drawerIsVisible =
                activityRule.activity.findViewById<DrawerLayout>(R.id.drawer_layout).isVisible
            val progressBarIsVisible =
                activityRule.activity.findViewById<ProgressBar>(R.id.loading_bar).isVisible

            if (!drawerIsVisible && !progressBarIsVisible) {
                onView(withId(android.R.id.button1))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
            }

            Thread.sleep(2000)

            true
        }
    }

    fun reopenApp() {
        val uiDevice = getInstance(getInstrumentation())
        pressRecentApps() // Open recent apps
        pressRecentApps() // Return to the app

        await().atMost(10, TimeUnit.SECONDS).until {
            try {
                onView(withId(R.id.drawer_layout))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                try {
                    onView(withId(R.id.loading_bar))
                        .check(matches(isDisplayed()))
                } catch (e: Exception) {
                    onView(withId(android.R.id.button1))
                        .inRoot(isDialog())
                        .check(matches(isDisplayed()))
                }
            }

            true
        }
    }

    private fun pressRecentApps(currentRetryCount: Int = 1, maxTimes: Int = 3) {
        val uiDevice = getInstance(getInstrumentation())
        var pressedOnce = uiDevice.pressRecentApps()

        if (pressedOnce) {
            Thread.sleep(1000)
            return
        } else {
            if (currentRetryCount < maxTimes) {
                pressRecentApps(currentRetryCount = currentRetryCount + 1)
            } else {
                throw RuntimeException("Could not press recent apps button")
            }
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

    fun retrieveEnteredText(accessCheckoutEditText: AccessCheckoutEditText): String {
        val property = accessCheckoutEditText.javaClass.getDeclaredField("editText")
        property.isAccessible = true
        val editText = property.get(accessCheckoutEditText) as EditText
        property.isAccessible = false
        return editText.text.toString()
    }

    fun onCardPanView(): ViewInteraction {
        return onView(ViewMatchers.withParent(withId(R.id.card_flow_text_pan)))
    }

    fun onCvcOnlyCvcView(): ViewInteraction {
        return onView(ViewMatchers.withParent(withId(R.id.cvc_flow_text_cvc)))
    }
}
