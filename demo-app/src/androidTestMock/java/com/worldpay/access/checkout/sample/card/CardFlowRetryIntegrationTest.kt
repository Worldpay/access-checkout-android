package com.worldpay.access.checkout.sample.card

import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import org.junit.Test

class CardFlowRetryIntegrationTest: AbstractCardFragmentTest() {
    @Test
    fun givenCallToCardBinServiceFailsItShouldRetryTheCallThenSuccessfulResponseIsReceived() {
        // Using specific card number that provides retry scenario see:
        // -post-card-bindetails-retry-scenario-1.1-fail.json
        // -post-card-bindetails-retry-scenario-1.2-success.json

        cardFragmentTestUtils
            .enterCardDetails(pan = "5163613613613613", expiryDate = "1299", cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.card_session_reference)).toString()
            )
    }

    @Test
    fun givenCallToCardBinServiceFailsAndRetriesAreExhaustedThenError() {
        // Using specific card number that provides retry scenario see:
        // -post-card-bindetails-retry-scenario-2.1-fail.json
        // -post-card-bindetails-retry-scenario-2.2-fail.json
        // -post-card-bindetails-retry-scenario-2.3-fail.json

        cardFragmentTestUtils
            .enterCardDetails(pan = "5131072454408923", expiryDate = "1299", cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.card_session_reference)).toString()
            )
    }
}
