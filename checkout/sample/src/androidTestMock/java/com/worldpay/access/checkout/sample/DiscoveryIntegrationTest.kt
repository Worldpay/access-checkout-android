package com.worldpay.access.checkout.sample

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.PAYMENTS_CVC
import com.worldpay.access.checkout.client.session.model.SessionType.VERIFIED_TOKENS
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.cvc.testutil.CvcFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.simulateRootResourceTemporaryServerError
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DiscoveryIntegrationTest {

    private val amexCard = "343434343434343"
    private val amexCvc = "1234"
    private val expiryDate = "1299"

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
            .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = expiryDate)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKENS to activityTestRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldRetryDiscoveryAndReturnSuccessfulResponse_whenDiscoveryFailsFirstTime_cvcFlow() {
        navigateTo(R.id.nav_cvc_flow)

        val cvcFragmentTestUtils = CvcFragmentTestUtils(activityTestRule)

        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "123")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(PAYMENTS_CVC to activityTestRule.activity.getString(R.string.payments_cvc_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

}
