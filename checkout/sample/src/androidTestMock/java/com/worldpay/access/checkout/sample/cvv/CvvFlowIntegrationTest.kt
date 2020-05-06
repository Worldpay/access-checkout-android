package com.worldpay.access.checkout.sample.cvv

import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.sample.ui.ProgressBar
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CvvFlowIntegrationTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(
        MainActivity::class.java)

    @Before
    fun setup() {
        navigateTo(R.id.nav_cvv_flow)
    }

    @Test
    fun shouldBeReturningValidResponse_whenEnteringValidCvv() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .check(matches(withAlpha(1.0f)))
            .perform(click(), typeText("123"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click())

        assertDisplaysResponseFromServer(
            mapOf(PAYMENTS_CVC_SESSION to activityRule.activity.getString(R.string.sessions_session_reference)).toString(),
            activityRule.activity.window.decorView
        )
    }

    @Test
    fun shouldDisableInputAndSubmitButton_afterClickingSubmit() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("111"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        uiObjectWithId(R.id.cvv_flow_btn_submit).click()

        assertTrue(ProgressBar(activityRule.activity).isLoading())

        wait { assertFalse { activityRule.activity.findViewById<TextView>(R.id.cvv_flow_text_cvv).isEnabled } }
        wait { assertTrue { activityRule.activity.findViewById<TextView>(R.id.cvv_flow_text_cvv).isVisible } }

        wait { assertFalse { activityRule.activity.findViewById<Button>(R.id.cvv_flow_btn_submit).isEnabled } }
        wait { assertTrue { activityRule.activity.findViewById<Button>(R.id.cvv_flow_btn_submit).isVisible } }

        assertDisplaysResponseFromServer(
            mapOf(PAYMENTS_CVC_SESSION to activityRule.activity.getString(R.string.sessions_session_reference)).toString(),
            activityRule.activity.window.decorView
        )
    }

    @Test
    fun shouldClearCvvInputAndDisableSubmit_afterReceivingResponse() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("111"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click())

        assertDisplaysResponseFromServer(
            mapOf(PAYMENTS_CVC_SESSION to activityRule.activity.getString(R.string.sessions_session_reference)).toString(),
            activityRule.activity.window.decorView
        )

        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .check(matches(withText("")))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }

    private fun wait(maxSecondsInMillis: Int = 1000, assertions: () -> Unit) {
        val pauseInterval = 100
        val maxTimes = maxSecondsInMillis / pauseInterval
        val maxWaitTime = pauseInterval * maxTimes

        for (i in 0..maxTimes) {
            try {
                assertions()
            } catch (exception: AssertionError) {
                if (i == maxTimes) {
                    throw AssertionError("Failed assertion after waiting $maxWaitTime ms", exception)
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