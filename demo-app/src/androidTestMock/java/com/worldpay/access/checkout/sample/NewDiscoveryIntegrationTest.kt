package com.worldpay.access.checkout.sample

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.sample.card.standard.testutil.NewCardFragmentTestUtils
import com.worldpay.access.checkout.sample.cvc.NewCvcFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.simulateRootResourceTemporaryServerError
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewDiscoveryIntegrationTest {
    // WIP

    private val amexCard = "343434343434343"
    private val amexCvc = "1234"
    private val expiryDate = "1299"

    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        simulateRootResourceTemporaryServerError()
    }

    @Test
    fun shouldRetryDiscoveryAndReturnSuccessfulResponse_whenDiscoveryFailsFirstTime_cardFlow() {
        navigateTo(R.id.nav_card_flow)

        val cardFragmentTestUtils = NewCardFragmentTestUtils(activityTestRule)

        activityTestRule.scenario.onActivity { activity ->
            cardFragmentTestUtils
                .isInInitialState()
                .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = expiryDate)
                .enabledStateIs(submitButton = true)
                .clickSubmitButton()
                .requestIsInProgress()
                .hasResponseDialogWithMessage(
                    mapOf(CARD to activity.getString(R.string.card_session_reference)).toString()
                )
                .closeDialog()
                .isInInitialState()
        }
    }

    @Test
    fun shouldRetryDiscoveryAndReturnSuccessfulResponse_whenDiscoveryFailsFirstTime_cvcFlow() {
        navigateTo(R.id.nav_cvc_flow)

        val cvcFragmentTestUtils = NewCvcFragmentTestUtils(activityTestRule)

        activityTestRule.scenario.onActivity { activity ->
            cvcFragmentTestUtils
                .isInInitialState()
                .enterCardDetails(cvc = "123")
                .clickSubmitButton()
                .requestIsInProgress()
                .hasResponseDialogWithMessage(
                    mapOf(CVC to activity.getString(R.string.cvc_session_reference)).toString()
                )
                .closeDialog()
                .isInInitialState()
        }
    }
}
