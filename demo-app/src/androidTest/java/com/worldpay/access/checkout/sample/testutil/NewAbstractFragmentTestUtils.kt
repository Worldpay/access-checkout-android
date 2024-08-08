package com.worldpay.access.checkout.sample.testutil

import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasTextColor
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import org.hamcrest.Matcher
import kotlin.test.assertTrue

abstract class NewAbstractFragmentTestUtils(protected val activityRule: ActivityScenarioRule<MainActivity>) {
    private fun progressBar() = UITestUtils.uiObjectWithId(R.id.loading_bar)

    protected fun progressBarIsVisible(msToWait: Long = 3000L): Boolean {
        return progressBar().waitForExists(msToWait)
    }

    protected fun progressBarNotVisible() {
        wait2 { assertTrue(progressBar().waitUntilGone(3000)) }
    }

    protected fun checkValidationState(
        viewMatcher: Matcher<View>,
        isValid: Boolean,
        field: String
    ) = also {
        wait2 {
            onView(viewMatcher).check(matches(hasTextColor(
                when (isValid) {
                    true -> R.color.SUCCESS
                    false -> R.color.FAIL
                }
            )))
        }
    }

    protected fun enterText(id: Int, text: String) {
        wait2 { onView(withId(id)).check(matches(isDisplayed())) }
        wait2 { onView(withId(id)).check(matches(isEnabled())) }

        val editTextUI = UITestUtils.uiObjectWithId(id)
        editTextUI.click()

        onView(ViewMatchers.withParent(withId(id))).perform(typeText(text))
            Log.i("msg", "edit text ui")

        onView(ViewMatchers.withParent(withId(id))).perform(closeSoftKeyboard())
    }

    protected fun enterTextOnViewWithId(text: String, id: Int) = also {
        onView(withId(id))
            .perform(typeText(text))
    }

    protected fun assertViewIsVisible(id: Int) {
        wait2 { onView(withId(id))
            .check(matches(isDisplayed()))  }
    }

    protected fun clearText(id: Int) {
        wait2 { onView(withId(id)).check(matches(isDisplayed())) }
        wait2 { onView(withId(id)).check(matches(isEnabled())) }

        val editTextUI = UITestUtils.uiObjectWithId(id)
        editTextUI.click()
        onView(withId(id)).perform(ViewActions.clearText())
    }

    protected fun setCursorPosition(
        id: Int,
        startSelection: Int,
        endSelection: Int
    ) {
        activityRule.scenario.onActivity { mainActivity ->
            mainActivity.runOnUiThread {
                onView(withId(id)).perform(
                    object : ViewAction {
                        override fun getConstraints(): Matcher<View> {
                            return withId(id)
                        }

                        override fun getDescription(): String {
                            return "set selection for view with $id"
                        }

                        override fun perform(uiController: UiController?, view: View?) {
                            if (view is EditText) {
                                view.setSelection(startSelection, endSelection)
                            }
                        }

                    }
                )
            }
        }
    }

    protected fun dialogHasText(text: String) {
        onView(withText(text))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    private fun color(colorId: Int) : Int {
        var colorInt = 0
        activityRule.scenario.onActivity { mainActivity ->
            colorInt = ResourcesCompat.getColor(mainActivity.resources, colorId, mainActivity.theme)
        }
        return colorInt
    }

    protected inline fun wait2(maxWaitTimeInMillis: Int = 1000, assertions: () -> Unit) {
        val pauseInterval = 100
        val maxTimes = maxWaitTimeInMillis / pauseInterval

        for (i in 0..maxTimes) {
            try {
                assertions()
            } catch (exception: AssertionError) {
                if (i == maxTimes) {
                    val seconds = maxWaitTimeInMillis / 1000
                    throw AssertionError(
                        "Failed assertion after waiting $seconds seconds: ${exception.message}",
                        exception
                    )
                } else {
                    Thread.sleep(pauseInterval.toLong())
                    Log.d(javaClass.simpleName, "Retrying assertion with pause interval: $pauseInterval")
                    continue
                }
            }
            break
        }
    }
}
