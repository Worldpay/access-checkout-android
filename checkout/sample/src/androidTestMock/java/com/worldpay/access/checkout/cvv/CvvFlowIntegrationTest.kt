package com.worldpay.access.checkout.cvv

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.testutil.UITestUtils.assertUiObjectExistsAndIsDisabled
import com.worldpay.access.checkout.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.ui.ProgressBar
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CvvFlowIntegrationTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

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
        assertUiObjectExistsAndIsDisabled(R.id.cvv_flow_text_cvv)

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

}