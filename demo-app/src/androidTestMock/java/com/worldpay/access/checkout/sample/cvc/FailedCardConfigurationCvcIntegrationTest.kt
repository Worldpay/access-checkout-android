package com.worldpay.access.checkout.sample.cvc

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvc.testutil.CvcFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.simulateCardConfigurationServerError
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FailedCardConfigurationCvcIntegrationTest {

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule = CardConfigurationErrorRule(MainActivity::class.java)

    private lateinit var cvcFragmentTestUtils: CvcFragmentTestUtils

    @Before
    fun setUp() {
        cvcFragmentTestUtils = CvcFragmentTestUtils(cardConfigurationErrorRule)
        rotatePortrait(cardConfigurationErrorRule)
        navigateTo(R.id.nav_cvc_flow)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun shouldSuccessfullyReturnResponse_whenCardConfigurationRetrievalFails() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "123")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CVC to cardConfigurationErrorRule.activity.getString(R.string.payments_cvc_session_reference)).toString()
            )
            .closeDialog()
            .cardDetailsAre(cvc = "")
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
