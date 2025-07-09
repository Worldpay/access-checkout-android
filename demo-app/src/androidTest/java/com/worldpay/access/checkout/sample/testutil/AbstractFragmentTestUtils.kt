package com.worldpay.access.checkout.sample.testutil

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class AbstractFragmentTestUtils(internal val activityRule: ActivityTestRule<MainActivity>) {

    private fun progressBar() = UITestUtils.uiObjectWithId(R.id.loading_bar)

    protected fun progressBarIsVisible() {
        wait { assertTrue(progressBar().waitForExists(3000)) }
        closeKeyboard()
    }

    protected fun progressBarNotVisible() {
        wait { assertTrue(progressBar().waitUntilGone(3000)) }
    }

    protected fun checkValidationState(
        accessCheckoutEditText: AccessCheckoutEditText,
        isValid: Boolean,
        field: String
    ) {
        if (isValid) {
            wait {
                assertEquals(
                    color(R.color.SUCCESS),
                    accessCheckoutEditText.currentTextColor,
                    "$field field expected to be valid"
                )
            }
        } else {
            wait {
                assertEquals(
                    color(R.color.FAIL),
                    accessCheckoutEditText.currentTextColor,
                    "$field field expected to be invalid"
                )
            }
        }
    }

    protected fun enterText(accessCheckoutEditText: AccessCheckoutEditText, text: String) {
        wait { assertTrue("${accessCheckoutEditText.id} - visibility state") { accessCheckoutEditText.isVisible } }
        wait { assertTrue("${accessCheckoutEditText.id} - enabled state") { accessCheckoutEditText.isEnabled } }
        wait {
            assertEquals(
                1.0f,
                accessCheckoutEditText.alpha,
                "${accessCheckoutEditText.id} - alpha state"
            )
        }

        val editTextUI = UITestUtils.uiObjectWithId(accessCheckoutEditText.id)
        editTextUI.click()
        activityRule.activity.runOnUiThread { accessCheckoutEditText.setText(text) }

        // Ensure the UI thread is idle
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val im = activity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        retry(3) {
            im.hideSoftInputFromWindow(accessCheckoutEditText.windowToken, 0)
        }

        // Wait for the keyboard to be fully hidden
        wait { assertTrue("Keyboard should be hidden") { !accessCheckoutEditText.isFocused } }

    }

    protected fun clearText(accessCheckoutEditText: AccessCheckoutEditText) {
        wait { assertTrue("${accessCheckoutEditText.id} - visibility state") { accessCheckoutEditText.isVisible } }
        wait { assertTrue("${accessCheckoutEditText.id} - enabled state") { accessCheckoutEditText.isEnabled } }
        wait {
            assertEquals(
                1.0f,
                accessCheckoutEditText.alpha,
                "${accessCheckoutEditText.id} - alpha state"
            )
        }

        val editTextUI = UITestUtils.uiObjectWithId(accessCheckoutEditText.id)
        editTextUI.click()
        activityRule.activity.runOnUiThread { accessCheckoutEditText.clear() }
    }

    protected fun setCursorPosition(
        accessCheckoutEditText: AccessCheckoutEditText,
        startSelection: Int,
        endSelection: Int
    ) {
        activityRule.activity.runOnUiThread {
            accessCheckoutEditText.setSelection(startSelection, endSelection)
        }
    }

    protected fun dialogHasText(text: String) {
        onView(withText(text))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    protected fun activity(): MainActivity = activityRule.activity

    private fun color(colorId: Int) =
        ResourcesCompat.getColor(activity().resources, colorId, activity().theme)

    protected fun <T : View> findById(id: Int): T {
        wait { assertNotNull(activity().findViewById<T>(id)) }
        return activity().findViewById(id)
    }

    protected fun wait(
        maxWaitTimeInMillis: Int = 20000,
        initialPauseInterval: Int = 100,
        assertions: () -> Unit
    ) {
        var pauseInterval = initialPauseInterval
        var elapsedTime = 0

        while (elapsedTime < maxWaitTimeInMillis) {
            try {
                assertions()
                return // Exit if assertions pass
            } catch (exception: AssertionError) {
                elapsedTime += pauseInterval
                if (elapsedTime >= maxWaitTimeInMillis) {
                    val seconds = maxWaitTimeInMillis / 1000
                    Log.e(
                        javaClass.simpleName,
                        "Assertion failed after $seconds seconds: ${exception.message}"
                    )
                    throw AssertionError(
                        "Failed assertion after waiting $seconds seconds: ${exception.message}",
                        exception
                    )
                } else {
                    Log.d(
                        javaClass.simpleName,
                        "Retrying assertion after ${pauseInterval}ms: ${exception.message}"
                    )
                    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                    Thread.sleep(pauseInterval.toLong())
                    pauseInterval *= 2 // Double the pause interval for exponential backoff
                }
            }
        }
    }

    private fun retry(times: Int, action: () -> Unit) {
        repeat(times) {
            try {
                action()
                return
            } catch (e: Exception) {
                Log.w(javaClass.simpleName, "Retrying action due to exception: ${e.message}")
            }
        }
    }
}
