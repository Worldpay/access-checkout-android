package com.worldpay.access.checkout.card

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.CardConfigurationMockStub.simulateCardConfigurationServerError
import com.worldpay.access.checkout.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardConfigurationIntegrationTest {

    private val luhnValidUnknownCard = "000000"
    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidUnknownCard = "111234"
    private val unknownCvv = "123456"
    private val month = "12"
    private val year = "99"

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule = CardConfigurationErrorRule(MainActivity::class.java)

    private lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(cardConfigurationErrorRule.activity)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun givenCardConfigurationCallFails_AndValidUnknownCardDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = luhnInvalidUnknownCard, cvv = unknownCvv, month = "13", year = year)
            .cardDetailsAre(pan = luhnInvalidUnknownCard, cvv = unknownCvv, month = "13", year = year)
            .hasNoBrand()
            .validationStateIs(pan = false, cvv = true, month = false, year = false)
            .enabledStateIs(submitButton = false)

        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidUnknownCard)
            .hasNoBrand()
            .validationStateIs(pan = true, cvv = true, month = false, year = false)
            .enabledStateIs(submitButton = false)

        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidMastercardCard, month = month)
            .hasNoBrand()
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to cardConfigurationErrorRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            cardConfigurationErrorRule.activity.window.decorView
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