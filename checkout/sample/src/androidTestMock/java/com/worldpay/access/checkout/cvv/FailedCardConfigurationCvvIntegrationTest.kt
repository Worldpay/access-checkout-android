package com.worldpay.access.checkout.cvv

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.CardConfigurationMockStub.simulateCardConfigurationServerError
import com.worldpay.access.checkout.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.testutil.UITestUtils.navigateTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FailedCardConfigurationCvvIntegrationTest {

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule =
        CardConfigurationErrorRule(MainActivity::class.java)

    @Before
    fun setUp() {
        navigateTo(R.id.nav_cvv_flow)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun shouldSuccessfullyReturnResponse_whenCardConfigurationRetrievalFails() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .check(matches(withAlpha(1.0f)))
            .perform(click(), ViewActions.typeText("123"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click())

        assertDisplaysResponseFromServer(cardConfigurationErrorRule.activity.getString(R.string.sessions_session_reference), cardConfigurationErrorRule.activity.window.decorView)
    }
}

class CardConfigurationErrorRule(activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mockserver to simulate a server error condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this error
        // response.
        simulateCardConfigurationServerError()
    }

}