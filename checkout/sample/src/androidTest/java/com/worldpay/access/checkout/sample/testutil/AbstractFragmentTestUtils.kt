package com.worldpay.access.checkout.sample.testutil

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class AbstractFragmentTestUtils(private val activityRule: ActivityTestRule<MainActivity>) {

    private fun progressBar() = UITestUtils.uiObjectWithId(R.id.loading_bar)

    protected fun progressBarIsVisible() {
        wait { assertTrue(progressBar().waitForExists(3000)) }
        UITestUtils.closeKeyboard()
    }

    protected fun progressBarNotVisible() {
        wait { assertTrue(progressBar().waitUntilGone(3000)) }
    }

    protected fun checkValidationState(editText: EditText, isValid: Boolean, field: String) {
        if (isValid) {
            wait { assertEquals(color(R.color.SUCCESS), editText.currentTextColor, "$field field expected to be valid") }
        } else {
            wait { assertEquals(color(R.color.FAIL), editText.currentTextColor, "$field field expected to be invalid") }
        }
    }

    protected fun enterText(editText: EditText, text: String) {
        wait { assertTrue("${editText.id} - visibility state") { editText.isVisible } }
        wait { assertTrue("${editText.id} - enabled state") { editText.isEnabled } }
        wait { assertEquals(1.0f, editText.alpha, "${editText.id} - alpha state") }

        val editTextUI = UITestUtils.uiObjectWithId(editText.id)
        editTextUI.click()
        if (editTextUI.text != text) {
            editTextUI.text = text
        }

        val im = activity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    protected fun activity() = activityRule.activity

    protected fun color(colorId: Int) =
        ResourcesCompat.getColor(activity().resources, colorId, activity().theme)

    protected fun <T: View> findById(id: Int): T {
        wait { assertNotNull(activity().findViewById<T>(id)) }
        return activity().findViewById(id)
    }

    protected fun wait(maxWaitTimeInMillis: Int = 1000, assertions: () -> Unit) {
        val pauseInterval = 100
        val maxTimes = maxWaitTimeInMillis / pauseInterval

        for (i in 0..maxTimes) {
            try {
                assertions()
            } catch (exception: AssertionError) {
                if (i == maxTimes) {
                    val seconds = maxWaitTimeInMillis / 1000
                    throw AssertionError("Failed assertion after waiting $seconds seconds: ${exception.message}", exception)
                } else {
                    Thread.sleep(pauseInterval.toLong())
                    LoggingUtils.debugLog(javaClass.simpleName, "Retrying assertion $assertions")
                    continue
                }
            }
            break
        }
    }

}