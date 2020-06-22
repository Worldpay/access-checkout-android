package com.worldpay.access.checkout.sample.cvc

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvc.testutil.CvcFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfigurationWithDelay
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DelayedCardConfigurationCvcIntegrationTest {

    @get:Rule
    var cardConfigurationRule: DelayedCardConfigurationRule =
        DelayedCardConfigurationRule(10000L, MainActivity::class.java)

    private lateinit var cvcFragmentTestUtils: CvcFragmentTestUtils

    @Before
    fun setup() {
        cvcFragmentTestUtils = CvcFragmentTestUtils(cardConfigurationRule)
        rotatePortrait(cardConfigurationRule)
        navigateTo(R.id.nav_cvc_flow)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationRule.activity)
    }

    @Test
    fun shouldSuccessfullyReturnResponse_whenCardConfigurationIsRetrievalSucceeds() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "123")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(PAYMENTS_CVC_SESSION to cardConfigurationRule.activity.getString(R.string.payments_cvc_session_reference)).toString()
            )
            .closeDialog()
            .cardDetailsAre(cvc = "")
            .enabledStateIs(submitButton = false)
    }

class DelayedCardConfigurationRule(private val timeoutMillis: Long,
                                     activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mockserver to simulate a long delay condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this delayed
        // response.
        stubCardConfigurationWithDelay(timeoutMillis.toInt())
    }

    }

}
