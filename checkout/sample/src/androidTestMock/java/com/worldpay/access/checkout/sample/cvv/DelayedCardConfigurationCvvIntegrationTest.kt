package com.worldpay.access.checkout.sample.cvv

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.model.*
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvv.testutil.CvvFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfigurationWithDelay
import com.worldpay.access.checkout.sample.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DelayedCardConfigurationCvvIntegrationTest {

    private val mastercardCvvValidationRule = CardValidationRule("^\\d{0,3}$", null, null, 3)
    private val mastercardPANValidationRule = CardValidationRule("^5\\d{0,15}$", null, null, 16)

    private val brands = listOf(
        CardBrand(
            "mastercard",
            listOf(CardBrandImage("image/svg+xml", "${MockServer.getBaseUrl()}/access-checkout/assets/mastercard.svg")),
            mastercardCvvValidationRule,
            listOf(mastercardPANValidationRule)
        )
    )

    private val defaults = CardDefaults(null, null, null, null)
    private val cardConfiguration = CardConfiguration(brands, defaults)

    @get:Rule
    var cardConfigurationRule: DelayedCardConfigurationRule =
        DelayedCardConfigurationRule(cardConfiguration, 10000L, MainActivity::class.java)

    private lateinit var cvvFragmentTestUtils: CvvFragmentTestUtils

    @Before
    fun setup() {
        cvvFragmentTestUtils = CvvFragmentTestUtils(cardConfigurationRule)
        navigateTo(R.id.nav_cvv_flow)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationRule.activity)
    }

    @Test
    fun shouldSuccessfullyReturnResponse_whenCardConfigurationIsRetrievalSucceeds() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "123")
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(PAYMENTS_CVC_SESSION to cardConfigurationRule.activity.getString(R.string.sessions_session_reference)).toString(),
            cardConfigurationRule.activity.window.decorView
        )

        cvvFragmentTestUtils
            .cardDetailsAre(cvv = "")
            .enabledStateIs(submitButton = false)
    }

    class DelayedCardConfigurationRule(private val cardConfiguration: CardConfiguration, private val timeoutMillis: Long,
                                         activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            // This card configuration rule adds stubs to mockserver to simulate a long delay condition on the card configuration endpoint.
            // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this delayed
            // response.
            stubCardConfigurationWithDelay(cardConfiguration, timeoutMillis.toInt())
        }

    }

}
