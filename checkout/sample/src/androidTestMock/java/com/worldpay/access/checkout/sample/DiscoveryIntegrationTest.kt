package com.worldpay.access.checkout.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.simulateRootResourceTemporaryServerError
import com.worldpay.access.checkout.sample.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DiscoveryIntegrationTest {

    private val amexCard = "343434343434343"
    private val amexCvv = "1234"
    private val month = "12"
    private val year = "99"

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        simulateRootResourceTemporaryServerError()
    }

    @Test
    fun shouldRetryDiscoveryAndReturnSuccessfulResponse_whenDiscoveryFailsFirstTime_cardFlow() {
        navigateTo(R.id.nav_card_flow)

        val cardFragmentTestUtils = CardFragmentTestUtils(activityTestRule)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to activityTestRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            activityTestRule.activity.window.decorView
        )

        cardFragmentTestUtils.isInInitialState()
    }

    @Test
    fun shouldRetryDiscoveryAndReturnSuccessfulResponse_whenDiscoveryFailsFirstTime_cvvFlow() {
        navigateTo(R.id.nav_cvv_flow)

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
            mapOf(PAYMENTS_CVC_SESSION to activityTestRule.activity.getString(R.string.sessions_session_reference)).toString(),
            activityTestRule.activity.window.decorView
        )
    }

}
