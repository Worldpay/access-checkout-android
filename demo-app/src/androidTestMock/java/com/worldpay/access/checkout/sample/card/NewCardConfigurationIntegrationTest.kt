package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN_FORMATTED
import com.worldpay.access.checkout.sample.card.standard.testutil.NewCardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.simulateCardConfigurationServerError
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NewCardConfigurationIntegrationTest {

    @get:Rule
    var cardConfigurationErrorRule = NewCardConfigurationErrorRule(ActivityScenarioRule(MainActivity::class.java))

    private lateinit var cardFragmentTestUtils: NewCardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = NewCardFragmentTestUtils(cardConfigurationErrorRule.innerRule)
    }

    @Test
    fun givenCardConfigCallFails_validKnownBrandCardDetails_returnsSuccessfulResponse() {
        cardFragmentTestUtils
            .enterCardDetails(pan = MASTERCARD_PAN, cvc = "1234", expiryDate = "1299")
            .cardDetailsAre(pan = MASTERCARD_PAN_FORMATTED, cvc = "1234", expiryDate = "12/99")
            .hasNoBrand()
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage( run {
                var ref = ""
                cardConfigurationErrorRule.innerRule.scenario.onActivity { activity ->
                    ref = activity.getString(R.string.card_session_reference)
                }
                mapOf(CARD to ref).toString()
            })
    }

    @Test
    fun givenCardConfigCallFails_validUnknownBrandCardDetails_returnsSuccessfulResponse() {
        cardFragmentTestUtils
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN, cvc = "1234", expiryDate = "1299")
            .cardDetailsAre(pan = VALID_UNKNOWN_LUHN_FORMATTED, cvc = "1234", expiryDate = "12/99")
            .hasNoBrand()
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage( run {
                var ref = ""
                cardConfigurationErrorRule.innerRule.scenario.onActivity { activity ->
                    ref = activity.getString(R.string.card_session_reference)
                }
                mapOf(CARD to ref).toString()
            })
    }
}

class NewCardConfigurationErrorRule(val innerRule: ActivityScenarioRule<MainActivity>) : ExternalResource() {

    override fun apply(base: Statement, description: Description): Statement {
        return super.apply(innerRule.apply(base, description), description)
    }

    override fun before() {
        super.before()
        // This card configuration rule adds stubs to mockserver to simulate a server error condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this error
        // response.
        simulateCardConfigurationServerError()
    }
}
