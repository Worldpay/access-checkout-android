package com.worldpay.access.checkout.sample.card

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN_FORMATTED
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.simulateCardConfigurationServerError
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardConfigurationIntegrationTest {

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule = CardConfigurationErrorRule(MainActivity::class.java)

    private lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(cardConfigurationErrorRule)
    }

    @Test
    fun givenCardConfigCallFails_validKnownBrandCardDetails_returnsSuccessfulSession() {
        cardFragmentTestUtils
            .enterCardDetails(pan = MASTERCARD_PAN, cvc = "1234", expiryDate = "1299")
            .cardDetailsAre(pan = MASTERCARD_PAN_FORMATTED, cvc = "1234", expiryDate = "12/99")
            .hasNoBrand()
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to cardConfigurationErrorRule.activity.getString(R.string.card_session_reference)).toString()
            )
    }

    @Test
    fun givenCardConfigCallFails_validUnknownBrandCardDetails_returnsSuccessfulSession() {
        cardFragmentTestUtils
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN, cvc = "1234", expiryDate = "1299")
            .cardDetailsAre(pan = VALID_UNKNOWN_LUHN_FORMATTED, cvc = "1234", expiryDate = "12/99")
            .hasNoBrand()
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to cardConfigurationErrorRule.activity.getString(R.string.card_session_reference)).toString()
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
