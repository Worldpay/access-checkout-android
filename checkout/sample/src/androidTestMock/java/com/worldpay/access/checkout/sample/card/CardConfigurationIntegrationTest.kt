package com.worldpay.access.checkout.sample.card

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.simulateCardConfigurationServerError
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardConfigurationIntegrationTest {

    private val luhnValidUnknownCard = "8888888888888888"
    private val luhnValidMastercardCard = "5555555555554444"
    private val unknownCvv = "1234"

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule = CardConfigurationErrorRule(
        MainActivity::class.java)

    private lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(cardConfigurationErrorRule)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun givenCardConfigCallFails_validKnownBrandCardDetails_returnsSuccessfulResponse() {
        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = unknownCvv, expiryDate = "1299")
            .cardDetailsAre(pan = luhnValidMastercardCard, cvv = unknownCvv, expiryDate = "12/99")
            .hasNoBrand()
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKEN_SESSION to cardConfigurationErrorRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
    }

    @Test
    fun givenCardConfigCallFails_validUnknownBrandCardDetails_returnsSuccessfulResponse() {
        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidUnknownCard, cvv = unknownCvv, expiryDate = "1299")
            .cardDetailsAre(pan = luhnValidUnknownCard, cvv = unknownCvv, expiryDate = "12/99")
            .hasNoBrand()
            .validationStateIs(pan = true, cvv = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKEN_SESSION to cardConfigurationErrorRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
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
