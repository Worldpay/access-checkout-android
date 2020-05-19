package com.worldpay.access.checkout.sample.cvv

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvv.testutil.CvvFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.simulateCardConfigurationServerError
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FailedCardConfigurationCvvIntegrationTest {

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule = CardConfigurationErrorRule(MainActivity::class.java)

    private lateinit var cvvFragmentTestUtils: CvvFragmentTestUtils

    @Before
    fun setUp() {
        cvvFragmentTestUtils = CvvFragmentTestUtils(cardConfigurationErrorRule)
        navigateTo(R.id.nav_cvv_flow)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun shouldSuccessfullyReturnResponse_whenCardConfigurationRetrievalFails() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "123")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(PAYMENTS_CVC_SESSION to cardConfigurationErrorRule.activity.getString(R.string.payments_cvc_session_reference)).toString()
            )
            .closeDialog()
            .cardDetailsAre(cvv = "")
            .enabledStateIs(submitButton = false)
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

}
